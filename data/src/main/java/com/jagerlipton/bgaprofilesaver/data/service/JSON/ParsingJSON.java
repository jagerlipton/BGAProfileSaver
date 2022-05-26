package com.jagerlipton.bgaprofilesaver.data.service.JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;

import java.io.IOException;
import java.util.ArrayList;

public class ParsingJSON {

    public static boolean isJSONValid(String jsonString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static ArrayList<ArduinoProfileListData> JSONToReplaceArrayList(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputItem json = mapper.readValue(jsonString, InputItem.class);
            ArrayList<ArrayItem> array = new ArrayList<>();
            array = json.array;
            if (array != null) {
                return InputDataToInputItemMapper(array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<ArduinoProfileListData> InputDataToInputItemMapper(ArrayList<ArrayItem> data) {
        ArrayList<ArduinoProfileListData> arrayListInputData = new ArrayList<>();

        for (ArrayItem arrayitem : data) {
            ArduinoProfileListData inputData = new ArduinoProfileListData();
            inputData.setKeyName(arrayitem.key);
            inputData.setValueName(arrayitem.value);
            arrayListInputData.add(inputData);
        }
        return arrayListInputData;
    }

    public static JSONCommand getJSONCommand(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            InputItem json = mapper.readValue(jsonString, InputItem.class);

            switch (json.command) {
                case "REPLACE": return JSONCommand.REPLACE;
                case "CLEAR": return JSONCommand.CLEAR;
                case "OK": return JSONCommand.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONCommand.NOPE;
    }

    //------------------------------------------------------------------------------------
    public static String listToShort(ArrayList<ArduinoProfileListData> inputList) {
        String data = "SHORT_PROFILE:";
        for (int i = 0; i < inputList.size(); i++) {
            data = data.concat(inputList.get(i).getValueName());
            data = data.concat(",");
        }
        return data;
    }

    public static String listToJSON(ArrayList<ArduinoProfileListData> inputList) {
        String data = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            OutputItem output = new OutputItem();
            output.array = ArduinoProfileListData.mapDataToArrayItem(inputList);
            output.command = "JSON_PROFILE";
            String jsonString = mapper.writeValueAsString(output);
            if (jsonString != null) data = jsonString;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


}
