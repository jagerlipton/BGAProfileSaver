package com.jagerlipton.bgaprofilesaver;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;


public class App extends Application {

    private static final App instance = new App();

    public static App getInstance() {
        return instance;
    }


//------------------------------------------------------------------------------------

    private static MutableLiveData<Boolean> portConnected = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> getPortStatus() {
        return portConnected;
    }

    public static void setPortStatus(Boolean status) {
        App.portConnected.postValue(status);
    }

//------------------------------------------------------------------------------------

    private static MutableLiveData<Integer> baudrateIndex = new MutableLiveData<>(0);

    public static MutableLiveData<Integer> getBaudrateIndex() {
        return baudrateIndex;
    }

    public static void setBaudrateIndex(Integer baudrateIndex) {
        App.baudrateIndex.postValue(baudrateIndex);
    }

//------------------------------------------------------------------------------------

    private static Integer baudrate = 9600;

    public static Integer getBaudrate() {
        return baudrate;
    }

    public static void setBaudrate(Integer baudrate) {
        App.baudrate = baudrate;
    }

//------------------------------------------------------------------------------------

    public static MutableLiveData<ArrayList<InputClass>> inputList = new MutableLiveData<>();

    public static void clearList(){
        ArrayList<InputClass> clearList = new ArrayList<>();
        inputList.postValue(clearList);
    }

//------------------------------------------------------------------------------------

    private static MutableLiveData<Boolean> isValidValues = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> getValidValues() {
        return isValidValues;
    }

    public static void setValidValues(Boolean validValues) {
        App.isValidValues.postValue(validValues);
    }
//------------------------------------------------------------------------------------

    public static Boolean isCanceled = false;

//------------------------------------------------------------------------------------

}
