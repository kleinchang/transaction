package com.commonwealth.banking.data.remote;


public class AppConfig {

    public static final String HOSTNAME = "https://www.dropbox.com/";
    public static final long CACHE_SIZE = 10 * 1024 * 1024;

    public final String hostname;

    public AppConfig(String hostname) {
        this.hostname = hostname;
    }
}
