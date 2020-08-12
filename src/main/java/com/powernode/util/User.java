package com.powernode.util;

public class User {
    private String name;
    private int value;

    public User() {
    }

    public User(int value,String name ) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
