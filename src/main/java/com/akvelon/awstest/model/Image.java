package com.akvelon.awstest.model;

import javax.persistence.*;

@Entity
@Table(name = "image_table")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public Image() {

    }

    public Image(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
}
