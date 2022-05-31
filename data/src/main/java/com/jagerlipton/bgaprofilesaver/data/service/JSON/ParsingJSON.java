package com.jagerlipton.bgaprofilesaver.data.service.JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static List<ArduinoProfileListData> JSONToReplaceArrayList(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputItem json = mapper.readValue(jsonString, InputItem.class);
            List<ArrayItem> array = new ArrayList<>();
            array = json.array;
            if (array != null) {
                return InputDataToInputItemMapper(array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ArduinoProfileListData> InputDataToInputItemMapper(List<ArrayItem> data) {
        List<ArduinoProfileListData> arrayListInputData = new ArrayList<>();
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
                case "REPLACE":
                    return JSONCommand.REPLACE;
                case "CLEAR":
                    return JSONCommand.CLEAR;
                case "READY":
                    return JSONCommand.READY;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONCommand.NOPE;
    }

    public static String listToString(List<ArduinoProfileListData> inputList) {
        String data = "SHORT_PROFILE:";
        for (int i = 0; i < inputList.size(); i++) {
            data = data.concat(inputList.get(i).getValueName());
            data = data.concat(",");
        }
        return data;
    }

    public static String listToJSONString(List<ArduinoProfileListData> inputList) {
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

    public static String commandToJSONString(String inputString) {
        String data = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            OutputItem output = new OutputItem();
            output.command = inputString;
             String jsonString = mapper.writeValueAsString(output);
            if (jsonString != null) data = jsonString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
