import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type");

    public static void main(String[] args) throws Exception {

        String path = System.getenv("PATH");

        String[] paths = path.split(":");

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

                    String file = findFile(paths, value);
                    if (!file.isEmpty()) {
                        System.out.println(value + " is " + file);
                        break;
                    }

                    System.out.println(value + ": not found");
                    break;
                case "echo":
                    System.out.println(tokens.length > 1 ? tokens[1] : "");
                    break;
                default:
                    doDefault(input);
            }
        }
    }

    private static void doDefault(String input) {
        String output = input + ": command not found";
        System.out.println(output);
    }

    private static String findFile(String[] paths, String filename) {

        for(String path: paths) {
            File folder = new File(path);
            FilenameFilter filter = (dir, name) -> name.equals(filename);
            File[] matches = folder.listFiles(filter);

            if (matches != null && matches.length > 0) {
                return matches[0].toString();
            }

        }
        return "";
    }
}
