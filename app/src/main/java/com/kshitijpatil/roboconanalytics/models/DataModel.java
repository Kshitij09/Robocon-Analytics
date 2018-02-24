package com.kshitijpatil.roboconanalytics.models;

public class DataModel {
    String heading;
    String value;

    public DataModel(String heading, String value) {
        this.heading = heading;
        this.value = value;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
