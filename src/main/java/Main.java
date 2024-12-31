import java.io.*;
import java.util.*;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type", "pwd", "cd", "ls");

    public static void main(String[] args) throws Exception {

        String path = System.getenv("PATH");

        File homePath = System.getenv("HOME").isEmpty() ? new File("") : new File(System.getenv("HOME"));

        String[] paths = path.split(":");

        File workingPath = new File("");

        ShellCommandHandler shell = new ShellCommandHandler(BUILTINS, paths, homePath, workingPath);

        while(true) {
            System.out.print("$ ");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            InputParser inputParser = new InputParser();
            inputParser.parseInput(input);
            shell.setCommand(inputParser.getCommand());

//            System.out.println(inputParser.getStdType());
//            System.out.println(Arrays.toString(inputParser.getArgs()));
//            System.out.println(inputParser.getOutputFilePath());

            if(inputParser.getStdType() == StdType.STDERR) {
                shell.setOutputFile(inputParser.getOutputFilePath());
            }

            switch (inputParser.getCommand()) {
                case "exit":
                    if (inputParser.getArgs().length > 0 && inputParser.getArgs()[0].equals("0")) {
                        return;
                    }
                    shell.handleNotFound(input);
                    break;

                case "type":
                    shell.handleTypeCommand(inputParser.getArgsString());
                    break;

                case "echo":
                    if(inputParser.getStdType() == StdType.STDOUT) {
                        int len = inputParser.getArgs().length;
                        shell.handleEchoOutputRedirect(inputParser.getArgs()[len - 1], inputParser.getOutputFilePath());
                        break;
                    }
                    shell.handleEchoCommand(inputParser.getArgsString());
                    break;

                case "pwd":
                    shell.handlePwdCommand();
                    break;

                case "cd":
                    shell.handleCdCommand(inputParser.getArgs()[0]);
                    break;

                case "cat":
                    if(input.charAt(0) == '\'' || input.charAt(0) == '\"') {
                        shell.handleExe(inputParser.getArgs()[1]);
                        break;
                    }

                    if(inputParser.getStdType() == StdType.STDOUT) {

                        shell.handleCatOutputRedirect(inputParser.getArgs(), inputParser.getOutputFilePath());
                        break;
                    }
                    shell.handleCatCommand(inputParser.getArgs());
                    break;

                case "ls":
                    if(inputParser.getStdType() == StdType.STDOUT) {
                        int len = inputParser.getArgs().length;

                        shell.handleLsOutputRedirect(inputParser.getArgs()[len - 1], inputParser.getOutputFilePath());
                        break;
                    }

                    shell.handleLs(inputParser.getArgs()[0]);
                    break;

                case "delete":
                    File file = new File(inputParser.getArgs()[0]);
                    file.delete();
                    break;

                default:
                    shell.handleDefault(inputParser.getCommand(), inputParser.getArgsString());
            }
        }
    }
}
