package com.jagerlipton.bgaprofilesaver.data.service.model;

public class Connection {

    private boolean portState;
    private boolean progressbarState;
    private String broadcast;
    private int VID;
    private int PID;
    private boolean supportedDevice;

    public Connection(boolean portState, boolean progressbarState, String broadcast, int VID, int PID, boolean supportedDevice) {
        this.portState = portState;
        this.progressbarState = progressbarState;
        this.broadcast = broadcast;
        this.VID = VID;
        this.PID = PID;
        this.supportedDevice = supportedDevice;
    }

    public Connection() {
    }

    public boolean isPortState() {
        return portState;
    }

    public void setPortState(boolean portState) {
        this.portState = portState;
    }

    public boolean isProgressbarState() {
        return progressbarState;
    }

    public void setProgressbarState(boolean progressbarState) {
        this.progressbarState = progressbarState;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    public int getVID() {
        return VID;
    }

    public void setVID(int VID) {
        this.VID = VID;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public boolean isSupportedDevice() {
        return supportedDevice;
    }

    public void setSupportedDevice(boolean supportedDevice) {
        this.supportedDevice = supportedDevice;
    }

    @Override
    public String toString() {
        return "{" + portState + "," + broadcast + "}";
    }

}


