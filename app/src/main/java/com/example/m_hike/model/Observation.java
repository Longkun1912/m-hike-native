package com.example.m_hike.model;

public class Observation {
    private int id;
    private String title;
    private String time;
    private String comment;

    public Observation() {
    }

    public Observation(int id, String title, String time, String comment) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
