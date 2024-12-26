import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type", "pwd", "cd");

    public static void main(String[] args) throws Exception {

        String path = System.getenv("PATH");

        String[] paths = path.split(":");

        File workingPath = new File("");

        while(true) {
            System.out.print("$ ");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            //split after first " "
            String[] tokens = input.split(" ", 2);


            switch (tokens[0]) {
                case "exit":
                    if (tokens.length > 1 && tokens[1].equals("0")) {
                        return;
                    }
                    doDefault(input);
                    break;

                case "type":
                    String value = tokens.length > 1 ? tokens[1] : "";
                    if( BUILTINS.contains(value) ) {
                        System.out.println(value + " is a shell builtin");
                        break;
                    }

                    File file = findFile(paths, value);
                    if (file != null) {
                        System.out.println(value + " is " + file);
                        break;
                    }

                    System.out.println(value + ": not found");
                    break;

                case "echo":
                    System.out.println(tokens.length > 1 ? tokens[1] : "");
                    break;

                case "pwd":
                    System.out.println(workingPath.getAbsolutePath());
                    break;

                case "cd":
                    String newPath = tokens.length > 1 ? tokens[1] : "";

                    String updatedPath = mergePaths(newPath, workingPath.getAbsolutePath());

                    File newWorkingPath = new File(updatedPath);

                    if (newWorkingPath.exists()) {
                        workingPath = newWorkingPath;
                    } else {
                        System.out.println("cd: " + updatedPath + ": No such file or directory");
                    }
                    break;

                default:
                    File processFile = findFile(paths, tokens[0]);
                    if(processFile != null) {
                        runProcess(tokens, processFile);
                        break;
                    }
                    doDefault(input);
            }
        }
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

    private static void runProcess(String[] tokens, File processFile) throws IOException {

        String args = tokens.length > 1 ? " " + tokens[1] : "";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", tokens[0] + args);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(processFile.getParent()));

        Process process = processBuilder.start();

        process.getInputStream().transferTo(System.out);
    }

    private static void doDefault(String input) {

        String output = input + ": command not found";
        System.out.println(output);
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
}
