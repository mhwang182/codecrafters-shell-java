import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ShellCommandHandler {

    private final Set<String> builtIns;

    private final String[] paths;

    private final File homePath;

    private File workingPath;

    public ShellCommandHandler(
            Set<String> builtIns,
            String[] paths,
            File homePath,
            File workingPath
    ) {
        this.builtIns = builtIns;
        this.paths = paths;
        this.homePath = homePath;
        this.workingPath = workingPath;
    }

    public void handleTypeCommand(String arg) {

        if( this.builtIns.contains(arg) ) {
            System.out.println(arg + " is a shell builtin");
            return;
        }

        File file = findFile(this.paths, arg);

        if (file != null) {
            System.out.println(arg + " is " + file);
            return;
        }

        System.out.println(arg + ": not found");
    }

    public void handleEchoCommand(String input) {
        System.out.println(input);
    }

    public void handlePwdCommand() {
        System.out.println(this.workingPath.getAbsolutePath());
    }

    public void handleCdCommand(String newPath) {
        if(newPath.length() == 1 && newPath.charAt(0) == '~') {
            workingPath = homePath;
            return;
        }

        String updatedPath = mergePaths(newPath, workingPath.getAbsolutePath());

        File newWorkingPath = new File(updatedPath);

        if (newWorkingPath.exists()) {
            workingPath = newWorkingPath;
        } else {
            System.out.println("cd: " + updatedPath + ": No such file or directory");
        }
    }

    public void handleNotFound(String input) {
        String output = input + ": command not found";
        System.out.println(output);
    }

    public void handleDefault(String command, String argString) throws IOException {

        File processFile = findFile(this.paths, command);

        String line = !argString.isEmpty() ? command + " " + argString : command;
        if(processFile != null) {
            runProcess(line, processFile);
            return;
        }
        handleNotFound(line);
    }

    public void handleCatCommand(String[] segments) throws IOException {

        for(String file: segments) {
            File currFile = new File(file);
            if(currFile.exists() && currFile.isFile()) {
                System.out.print(Files.readString(currFile.toPath()));
            } else {
                System.out.println("cat: " + currFile.getPath() + ": No such file or directory");
                return;
            }
        }
    }

    public void handleExe(String path) throws IOException {

        File file = new File(path);
        if(file.exists() && file.isFile()) {
            System.out.print(Files.readString(file.toPath()));
        }
    }

    public void handleLsOutputRedirect(String path1, String path2) throws IOException {

        File file1 = new File(path1);
        File file2 = new File(path2);

        if(file1.exists() && file1.isFile()) {
            String content = Files.readString(file1.toPath());

            overwriteFile(file2, content);
        }
    }

    public void handleEchoOutputRedirect(String content, String outputPath) throws IOException {

        File outputFile = new File(outputPath);
        overwriteFile(outputFile, content);
    }

    private static void overwriteFile(File outputFile, String content) throws IOException {

        outputFile.createNewFile();
        DataOutputStream outputStream= new DataOutputStream(new FileOutputStream(outputFile,false));

        outputStream.write(content.getBytes());
        outputStream.close();
    }

    private static void runProcess(String commandLine, File processFile) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", commandLine);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(processFile.getParent()));

        Process process = processBuilder.start();

        process.getInputStream().transferTo(System.out);
    }

    private static File findFile(String[] paths, String filename) {

        for(String path: paths) {
            File folder = new File(path);
            FilenameFilter filter = (dir, name) -> name.equals(filename);
            File[] matches = folder.listFiles(filter);

            if (matches != null && matches.length > 0) {
                return matches[0];
            }

        }
        return null;
    }

    private static String mergePaths(String newPath, String currentPath) {

        if(newPath.charAt(0) != '.') {
            return newPath;
        }

        List<String> oldParts = new ArrayList<>(Arrays.asList(currentPath.split("/")));

        String[] newParts = newPath.split("/");

        for (String newPart : newParts) {

            if (newPart.equals(".")) {
                continue;
            }

            if (newPart.equals("..")) {
                oldParts.removeLast();
                continue;
            }

            oldParts.add(newPart);
        }

        return String.join("/", oldParts);
    }

}
