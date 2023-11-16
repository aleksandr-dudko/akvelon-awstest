package com.akvelon.awstest.model;

public class Image {
    private Long id;
    private String name;

    public Image(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
