package com.jagerlipton.bgaprofilesaver.domain.model;

import java.util.ArrayList;

public class CommandModel {

    public enum Commands {
        COMMAND_GET_PROFILE, COMMAND_SAVE_PROFILE, SHORT_PROFILE, JSON_PROFILE
    }
    private Commands command;
    private ArrayList<ArduinoProfileListDomain> list;

    public CommandModel() {
    }

    public CommandModel(Commands command, ArrayList<ArduinoProfileListDomain> list) {
        this.command = command;
        this.list = list;
    }

    public Commands getCommand() {
        return command;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public ArrayList<ArduinoProfileListDomain> getList() {
        return list;
    }

    public void setList(ArrayList<ArduinoProfileListDomain> list) {
        this.list = list;
    }
}
