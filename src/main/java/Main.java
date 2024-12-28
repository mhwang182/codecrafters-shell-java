import java.io.*;
import java.util.*;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type", "pwd", "cd");

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
                    if (inputParser.getArgs().length > 1 && inputParser.getArgs()[0].equals("0")) {
                        return;
                    }
                    shell.handleNotFound(input);
                    break;

                case "type":
                    shell.handleTypeCommand(inputParser.getArgsString());
                    break;

                case "echo":
                    shell.handleEchoCommand(inputParser.getArgsString());
                    break;

                case "pwd":
                    shell.handlePwdCommand();
                    break;

                case "cd":
                    shell.handleCdCommand(inputParser.getArgs()[0]);
                    break;

                case "cat":
                    System.out.println(Arrays.toString(inputParser.getArgs()));
                    shell.handleCatCommand(inputParser.getArgs());
                    break;

                default:
                    shell.handleDefault(inputParser.getCommand(), inputParser.getArgsString());
            }
        }
    }
}
