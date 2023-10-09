package com.example.tawriqapp.Model;

import java.io.Serializable;

public class Program implements Serializable {

    private String
            programName;

    public Program() {
    }

    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
