package com.jagerlipton.bgaprofileeditor.domain.model;


import java.util.List;

public class CommandModel {

    public enum Commands {
        COMMAND_GET_PROFILE, COMMAND_SAVE_PROFILE, SHORT_PROFILE, JSON_PROFILE
    }
    private Commands command;
    private List<ArduinoProfileListDomain> list;

    public CommandModel() {
    }

    public CommandModel(Commands command, List<ArduinoProfileListDomain> list) {
        this.command = command;
        this.list = list;
    }

    public Commands getCommand() {
        return command;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public List<ArduinoProfileListDomain> getList() {
        return list;
    }

    public void setList(List<ArduinoProfileListDomain> list) {
        this.list = list;
    }
}
