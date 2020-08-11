package com.example.githubtrailblazer;

public class FlagMaster {
    private boolean ghFlag;
    private boolean glFlag;
    private boolean isInitialized = false;
    private static volatile FlagMaster instance = null;

    private FlagMaster() {
    }

    public static FlagMaster getInstance() {
        if (instance == null) {
            synchronized (FlagMaster.class) {
                if (instance == null) {
                    instance = new FlagMaster();
                }
            }
        }
        return instance;
    }

    public boolean getGHFlag() {
        if (isInitialized) {
            return ghFlag;
        } else {
            return false;
        }
    }

    public boolean getGLFlag() {
        if (isInitialized) {
            return glFlag;
        } else {
            return false;
        }
    }

    public void initialize(boolean gh, boolean gl) {
        this.ghFlag = gh;
        this.glFlag = gl;
        isInitialized = true;
    }

    /*private boolean ghFlag;
    private boolean glFlag;

    public FlagMaster(boolean gh, boolean gl) {
        ghFlag = gh;
        glFlag = gl;
    }

    public boolean getGhFlag() {
        return ghFlag;
    }

    public void setGhFlag(boolean ghFlag) {
        this.ghFlag = ghFlag;
    }

    public boolean getGlFlag() {
        return glFlag;
    }

    public void setGlFlag(boolean glFlag) {
        this.glFlag = glFlag;
    }

     */
}
