import java.util.ArrayList;
import java.util.List;

public class InputParser {

    private String command;

    private String[] args;

    private String argsString;

    public InputParser() {
        this.command = "";
        this.args = new String[0];
        this.argsString = "";
    }

    public String getCommand() {
        return this.command;
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getArgsString() {
        return this.argsString;
    }

    public void parseInput(String input) {

        input = input + " ";
        int i = 0;

        String commandString = "";

        StringBuilder sb = new StringBuilder();

        List<String> argList = new ArrayList<>();

        boolean parsingSingle = false;
        boolean parsingDouble = false;

        while(i < input.length()) {
            if(!parsingSingle && !parsingDouble && Character.isWhitespace(input.charAt(i))) {

                if(!sb.isEmpty()) {
                    if(commandString.isEmpty()) {
                        commandString = sb.toString();
                    } else {
                        argList.add(sb.toString());
                    }
                    sb = new StringBuilder();
                }
                i++;
                continue;
            }

            if(!parsingDouble && input.charAt(i) == '\'') {

                if(!sb.isEmpty()) {
                    argList.add(sb.toString());
                    sb = new StringBuilder();
                    parsingSingle = false;
                } else {
                    parsingSingle = true;
                }
                i++;
                continue;
            }

            if(input.charAt(i) == '\"') {

                if(!sb.isEmpty()) {
                    argList.add(sb.toString());
                    sb = new StringBuilder();
                    parsingDouble = false;
                } else {
                    parsingDouble = true;
                }
                i++;
                continue;
            }

            if(input.charAt(i) == '\\') {

                if(i + 1 < input.length()) {
                    if(input.charAt(i + 1) == '\\' || input.charAt(i + 1) == '$' || input.charAt(i + 1) == '"') {
                        sb.append(input.charAt(i + 1));
                    } else if(input.charAt(i + 1) == 'n') {
                        sb.append(System.lineSeparator());
                    }
                    i += 2;
                    continue;
                }
            }

            sb.append(input.charAt(i));
            i++;
        }

        this.command = commandString;
        this.args = argList.toArray(new String[0]);
        this.argsString = String.join(" ", argList);
    }
}
