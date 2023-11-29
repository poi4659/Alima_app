package com.example.alima_test;

public class Feedback {
    private String feedback;

    public Feedback() {
        // 필수 디폴트 생성자
    }

    public Feedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
