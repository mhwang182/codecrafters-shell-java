import java.util.Scanner;

public class Main {
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
                case "echo":
                    System.out.println(tokens[1]);
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
