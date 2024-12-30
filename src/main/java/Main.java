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
                    if(inputParser.getRedirectSymbolIndex() > -1) {
                        int index = inputParser.getRedirectSymbolIndex();
                        shell.handleEchoOutputRedirect(inputParser.getArgs()[index - 1], inputParser.getArgs()[index + 1]);
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
                    shell.handleCatCommand(inputParser.getArgs());
                    break;

                case "ls":
                    if(inputParser.getRedirectSymbolIndex() > -1) {
                        int index = inputParser.getRedirectSymbolIndex();
                        shell.handleLsOutputRedirect(inputParser.getArgs()[index - 1], inputParser.getArgs()[index + 1]);
                        break;
                    }
                    break;

                default:
                    shell.handleDefault(inputParser.getCommand(), inputParser.getArgsString());
            }
        }
    }
}
