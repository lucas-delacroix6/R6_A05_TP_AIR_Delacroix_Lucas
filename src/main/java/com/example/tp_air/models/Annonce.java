package com.example.tp_air.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Annonce {
    private int id;
    private final String title;
    private final String description;
    private final String address;
    private final String mail;
    private final Timestamp date;

    public Annonce(String title, String description, String address, String mail) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.mail = mail;
        this.date = Timestamp.valueOf(LocalDateTime.now());
    }

    public Annonce(int id, String title, String description, String address, String mail, Timestamp date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.mail = mail;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getMail() { return mail; }
    public Timestamp getDate() { return date; }

    public void setId(int id) { this.id = id; }

}
