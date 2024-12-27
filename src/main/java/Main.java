import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type", "pwd", "cd", "cat");

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

            //split after first " "
            String[] tokens = input.split(" ", 2);

            String command = tokens[0];
            String arg = tokens.length > 1 ? tokens[1] : "";

            arg = replaceSingleQuotes(arg);

            switch (command) {
                case "exit":
                    if (tokens.length > 1 && arg.equals("0")) {
                        return;
                    }
                    shell.handleNotFound(input);
                    break;

                case "type":
                    shell.handleTypeCommand(arg);
                    break;

                case "echo":
                    shell.handleEchoCommand(arg);
                    break;

                case "pwd":
                    shell.handlePwdCommand();
                    break;

                case "cd":
                    shell.handleCdCommand(arg);
                    break;

                case "cat":
                    shell.handleCatCommand(arg);
                    break;

                default:
                    shell.handleDefault(tokens);
            }
        }
    }

    private static String replaceSingleQuotes(String input) {
        String output = input;
        Pattern matcherPattern = Pattern.compile("'([^']*)'");
        Matcher matcher = matcherPattern.matcher(input);
        while(matcher.find()){
            String match = matcher.group();
            output = output.replace(match, match.substring(1, match.length() - 1));
        }

        return output;
    }
}
