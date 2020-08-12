package com.git_trailblazer.v1;
/**
 * FlagMaster class for defining feature flags to be used
 * e.g., ghFlag = true; glFlag = false  ==> only use GitHub API data
 * Follows the (lazy-loading) Singleton pattern.
 */
public class FlagMaster {
    private boolean ghFlag;
    private boolean glFlag;
    private boolean isInitialized = false;

    // Implementing the Singleton pattern with a private initializer.
    private static volatile FlagMaster instance = null;

    private FlagMaster() {
    }

    // function adapted from https://en.wikipedia.org/wiki/Singleton_pattern#cite_ref-6
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
}
