package com.kshitijpatil.roboconanalytics.models;


import java.util.ArrayList;

public class Team {
    public String name;
    ArrayList<Note> notes;
    long index;

    public Team() {
    }

    public Team(String name, long index) {
        this.name = name;
        this.index = index;
        notes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
    public void addNote(Note note){
        if (notes == null)
            notes = new ArrayList<>();
        notes.add(note);
    }
}
