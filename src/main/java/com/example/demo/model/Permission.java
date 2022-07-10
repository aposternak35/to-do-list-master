package com.example.demo.model;

public enum Permission {
    DEVELOPERS_READ("developers.read"),
    DEVELOPERS_WRITE("developers.write");
    private final String permission;

    Permission(String permisssion) {
        this.permission = permisssion;
    }

    public String getPermission() {
        return permission;
    }
}
