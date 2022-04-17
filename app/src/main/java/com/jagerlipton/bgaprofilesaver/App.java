package com.jagerlipton.bgaprofilesaver;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;


public class App extends Application {

    private static final App instance = new App();

    public static App getInstance() {
        return instance;
    }


//------------------------------------------------------------------------------------
   // state Connection
    private static MutableLiveData<Boolean> portState = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> isPortState() {
        return portState;
    }

    public static void setPortState(Boolean status) {
        App.portState.postValue(status);
    }

//------------------------------------------------------------------------------------
    // state  1 - Clear; 2 - wait data; 3 - Fully views
    private static MutableLiveData<Integer> screenState = new MutableLiveData<Integer>(1);

    public static LiveData<Integer> getScreenState() {
        return screenState;
    }

    public static void setScreenState(Integer status) {
        App.screenState.postValue(status);
    }

//------------------------------------------------------------------------------------
    // state Valid/Invalid editTexts
    private static MutableLiveData<Boolean> validValuesState = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> isValidValuesState() {
        return validValuesState;
    }

    public static void setValidValues(Boolean validValues) {
        App.validValuesState.postValue(validValues);
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

    public static void addLines (ArrayList<InputClass> inputList) {
        inputList.addAll(inputList);


    }


//------------------------------------------------------------------------------------



}
