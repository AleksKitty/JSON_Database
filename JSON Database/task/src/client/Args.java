package client;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = "-in", description = "File with input")
    private String inputFile;

    @Parameter(names = "-t", description = "Type of command")
    private String command;

    @Parameter(names = "-k", description = "Index of the cell")
    private String index;

    @Parameter(names = "-v", description = "Text message")
    private String textValue;

    public String getInputFile() {
        return inputFile;
    }

    public String getCommand() {
        if (command == null || command.isEmpty()) {
            return "";
        } else {
            return command;
        }
    }

    public String getIndex() {
        if (index == null || index.isEmpty()) {
            return "";
        } else {
            return index;
        }
    }

    public String getTextValue() {
        if (textValue == null || textValue.isEmpty()) {
            return "";
        } else {
            return textValue;
        }
    }
}
