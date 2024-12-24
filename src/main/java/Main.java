import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        while(true) {
            System.out.print("$ ");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            String output = input + ": command not found";
            System.out.println(output);
        }
    }
}
