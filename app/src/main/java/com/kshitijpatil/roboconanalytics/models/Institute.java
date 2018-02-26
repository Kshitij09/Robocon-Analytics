package com.kshitijpatil.roboconanalytics.models;

public class Institute {
    String name;
    long index;
    float tz1_successful_hits;
    float tz1_total_hits;
    float tz1_accuracy;
    float tz2_successful_hits;
    float tz2_total_hits;
    float tz2_accuracy;
    float tz3_successful_hits;
    float tz3_total_hits;
    float tz3_accuracy;
    float successful_pass;
    float total_pass;
    float pass_accuracy;

    public Institute() {
    }

    public Institute(String name, long index) {
        this.name = name;
        this.index = index;
        tz1_successful_hits = 0.0f;
        tz1_total_hits = 0.0f;
        tz1_accuracy = 0.0f;
        tz2_successful_hits = 0.0f;
        tz2_total_hits = 0.0f;
        tz2_accuracy = 0.0f;
        tz3_successful_hits = 0.0f;
        tz3_total_hits = 0.0f;
        tz3_accuracy = 0.0f;
        successful_pass = 0.0f;
        total_pass = 0.0f;
        pass_accuracy = 0.0f;
    }

    public float getSuccessful_pass() {
        return successful_pass;
    }

    public void setSuccessful_pass(float successful_pass) {
        this.successful_pass = successful_pass;
    }

    public float getTotal_pass() {
        return total_pass;
    }

    public void setTotal_pass(float total_pass) {
        this.total_pass = total_pass;
    }

    public float getPass_accuracy() {
        return pass_accuracy;
    }

    public void setPass_accuracy(float pass_accuracy) {
        this.pass_accuracy = pass_accuracy;
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

    public float getTz1_successful_hits() {
        return tz1_successful_hits;
    }

    public void setTz1_successful_hits(float tz1_successful_hits) {
        this.tz1_successful_hits = tz1_successful_hits;
    }

    public float getTz1_total_hits() {
        return tz1_total_hits;
    }

    public void setTz1_total_hits(float tz1_total_hits) {
        this.tz1_total_hits = tz1_total_hits;
    }

    public float getTz1_accuracy() {
        return tz1_accuracy;
    }

    public void setTz1_accuracy(float tz1_accuracy) {
        this.tz1_accuracy = tz1_accuracy;
    }

    public float getTz2_successful_hits() {
        return tz2_successful_hits;
    }

    public void setTz2_successful_hits(float tz2_successful_hits) {
        this.tz2_successful_hits = tz2_successful_hits;
    }

    public float getTz2_total_hits() {
        return tz2_total_hits;
    }

    public void setTz2_total_hits(float tz2_total_hits) {
        this.tz2_total_hits = tz2_total_hits;
    }

    public float getTz2_accuracy() {
        return tz2_accuracy;
    }

    public void setTz2_accuracy(float tz2_accuracy) {
        this.tz2_accuracy = tz2_accuracy;
    }

    public float getTz3_successful_hits() {
        return tz3_successful_hits;
    }

    public void setTz3_successful_hits(float tz3_successful_hits) {
        this.tz3_successful_hits = tz3_successful_hits;
    }

    public float getTz3_total_hits() {
        return tz3_total_hits;
    }

    public void setTz3_total_hits(float tz3_total_hits) {
        this.tz3_total_hits = tz3_total_hits;
    }

    public float getTz3_accuracy() {
        return tz3_accuracy;
    }

    public void setTz3_accuracy(float tz3_accuracy) {
        this.tz3_accuracy = tz3_accuracy;
    }

    public void increamentTZ1TotalHits() {
        tz1_total_hits += 1;
    }

    public void increamentTZ1SuccessfulHits() {
        tz1_successful_hits += 1;
    }

    public void calculateTZ1Accuracy() {
        tz1_accuracy = (tz1_successful_hits / tz1_total_hits) * 100;
    }

    public void increamentTZ2TotalHits() {
        tz2_total_hits += 1;
    }

    public void increamentTZ2SuccessfulHits() {
        tz2_successful_hits += 1;
    }

    public void calculateTZ2Accuracy() {
        tz2_accuracy = (tz2_successful_hits / tz2_total_hits) * 100;
    }

    public void increamentTZ3TotalHits() {
        tz3_total_hits += 1;
    }

    public void increamentTZ3SuccessfulHits() {
        tz3_successful_hits += 1;
    }

    public void calculateTZ3Accuracy() {
        tz3_accuracy = (tz3_successful_hits / tz3_total_hits) * 100;
    }

    public void increamentSuccessfulPass() {
        successful_pass += 1;
    }

    public void increamentTotalPass() {
        total_pass += 1;
    }

    public void calculatePassAccuracy() {
        pass_accuracy = (successful_pass / total_pass) * 100;
    }

    public void decrementTZ1SuccessfulHits(){
        if (tz1_successful_hits > 0)
            tz1_successful_hits -= 1;
    }
    public void decrementTZ1TotalHits(){
        if (tz1_total_hits > 0)
            tz1_total_hits -= 1;
    }

    public void decrementTZ2SuccessfulHits(){
        if (tz2_successful_hits > 0)
            tz2_successful_hits -= 1;
    }
    public void decrementTZ2TotalHits(){
        if (tz2_total_hits > 0)
            tz2_total_hits -= 1;
    }
    public void decrementTZ3SuccessfulHits(){
        if (tz3_successful_hits > 0)
            tz3_successful_hits -= 1;
    }
    public void decrementTZ3TotalHits(){
        if (tz3_total_hits > 0)
            tz3_total_hits -= 1;
    }
    public void decrementSuccessfulPass(){
        if (successful_pass > 0)
            successful_pass -= 1;
    }
    public void decrementTotalPass(){
        if (total_pass > 0)
        total_pass -= 1;
    }

}