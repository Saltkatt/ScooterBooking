package com.wirelessiths;

public class Settings {

    private String buffer;
    private String maxDuration;

    public Settings(String buffer, String maxDuration) {
        this.buffer = buffer;
        this.maxDuration = maxDuration;
    }

    public Settings() {

    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }



}
