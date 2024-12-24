import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type");

    public static void main(String[] args) throws Exception {
        
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
                    } else {
                        System.out.println(value + ": not found");
                    }
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
}
