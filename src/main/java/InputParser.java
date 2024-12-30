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
                    sb.setLength(0);
                }
                i++;
                continue;
            }

            if(!parsingSingle && input.charAt(i) == '\\') {
                if(!parsingDouble) {
                    if(i + 1 < input.length()) {
                        sb.append(input.charAt(i + 1));
                        i += 2;
                        continue;
                    }
                }

                if(parsingDouble && i + 1 < input.length()) {
                    char c = input.charAt(i + 1);
                    if(c == '\\' || c == '$' || c == '"' || c == '\n') {
                        sb.append(input.charAt(i + 1));
                        i += 2;
                    } else {
                        sb.append(input.charAt(i));
                        i++;
                    }
                    continue;
                }
            }

            if(!parsingDouble && input.charAt(i) == '\'') {

                //first non space character is single quote
                if(commandString.isEmpty() && !parsingSingle) {
                    commandString = "cat";
                }

                if(!sb.isEmpty()) {
                    argList.add(sb.toString());
                    sb.setLength(0);
                    parsingSingle = false;
                } else {
                    parsingSingle = true;
                }
                i++;
                continue;
            }

            if(!parsingSingle && input.charAt(i) == '\"') {

                //first non space character is double quote
                if(commandString.isEmpty() && !parsingDouble) {
                    commandString = "cat";
                }

                //characters after last quote
                if(parsingDouble && i + 1 < input.length() && Character.isLetterOrDigit(input.charAt(i + 1))) {
                    i++;
                    parsingDouble = false;
                    continue;
                }
                if(!sb.isEmpty()) {
                    argList.add(sb.toString());
                    sb.setLength(0);
                    parsingDouble = false;
                } else {
                    parsingDouble = true;
                }
                i++;
                continue;
            }

            sb.append(input.charAt(i));
            i++;
        }

        if(!parsingSingle && !parsingDouble && !sb.isEmpty()) {
            if(commandString.isEmpty()) {
                commandString = sb.toString();
            } else {
                argList.add(sb.toString());
            }
        }

        this.command = commandString;
        this.args = argList.toArray(new String[0]);
        this.argsString = String.join(" ", argList);
    }
}
