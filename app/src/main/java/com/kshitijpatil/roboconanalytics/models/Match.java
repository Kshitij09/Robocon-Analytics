package com.kshitijpatil.roboconanalytics.models;

import java.util.ArrayList;

public class Match {
    public Institute institute;
    long index;
    long timestamp;
    ArrayList<Note> notes;
    public static final String OPEN = "open";
    public static final String CLOSE = "closed";
    String status;

    public Match() {

    }

    public Match(long index, long timestamp, Institute institute) {
        this.index = index;
        this.timestamp = timestamp;
        this.institute = institute;
        notes = new ArrayList<>();
        status = OPEN;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
    public void addNote(Note note){
        if (notes == null)
            notes = new ArrayList<>();
        notes.add(note);
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
}
