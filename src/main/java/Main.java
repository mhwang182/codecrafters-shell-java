import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String[] segments = getLiteralSegments(tokens[1]);

                    shell.handleCatCommand(segments);
                    break;

                default:
                    shell.handleDefault(tokens);
            }
        }
    }

    private static String[] getLiteralSegments(String input) {
        String output = input;
        Pattern matcherPattern = Pattern.compile("'([^']*)'");
        Matcher matcher = matcherPattern.matcher(input);

        int matchIndex = 0;

        Map<String, String> matchesMap = new HashMap<>();
        //find all the literals and mark
        while(matcher.find()) {
            String match = matcher.group();
            String marker = matchIndex + match.replace(" ", "");
            matchesMap.put(marker, match);
            output = output.replace(match, marker);
            matchIndex++;
        }

        //remove all extra spaces
        output = output.trim().replaceAll(" +", " ");

        String[] segments = output.split(" ");

        for(int i = 0; i < segments.length; i++) {
            if(matchesMap.containsKey(segments[i])) {
                String literal = matchesMap.get(segments[i]);
                segments[i] = literal.substring(1, literal.length() - 1);
            }
        }
        return segments;
    }


    private static String replaceSingleQuotes(String input) {
        String output = input;
        Pattern matcherPattern = Pattern.compile("'([^']*)'");
        Matcher matcher = matcherPattern.matcher(input);

        int matchIndex = 0;

        Map<String, String> matchesMap = new HashMap<>();
        //find all the literals and mark
        while(matcher.find()) {
            String match = matcher.group();
            String marker = matchIndex + match.replace(" ", "");
            matchesMap.put(marker, match);
            output = output.replace(match, marker);
            matchIndex++;
        }

        //remove all extra spaces
        output = output.trim().replaceAll(" +", " ");

        for(String key: matchesMap.keySet()) {
            String match = matchesMap.get(key);
            output = output.replace(key, match.substring(1, match.length() - 1));
        }

        return output;
    }
}
