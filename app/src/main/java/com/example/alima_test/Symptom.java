package com.example.alima_test;

class Symptom {
    private String date;
    private String symptom;

    public Symptom(String date, String symptom) {
        this.date = date;
        this.symptom = symptom;
    }

    public String getDate() {
        return date;
    }

    public String getSymptom() {
        return symptom;
    }
}
