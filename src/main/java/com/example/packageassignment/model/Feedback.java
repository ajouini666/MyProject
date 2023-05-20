package com.example.packageassignment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Objects;

@Document(collection = "feedbacks")
public class Feedback {
    @Id
    private String id;
    private String fullName;
    private String recipientEmail;
    private int rating;
    private String feedback;
    private String useAgain;

    public Feedback(String id, String recipientEmail, String fullName, int rating, String feedback, String useAgain) {
        this.id = id;
        this.recipientEmail = recipientEmail;
        this.fullName = fullName;
        this.rating = rating;
        this.feedback = feedback;
        this.useAgain = useAgain;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Feedback fullName(String fullName) {
        setFullName(fullName);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Feedback)) {
            return false;
        }
        Feedback feedback = (Feedback) o;
        return Objects.equals(id, feedback.id) && Objects.equals(fullName, feedback.fullName)
                && Objects.equals(recipientEmail, feedback.recipientEmail) && rating == feedback.rating
                && Objects.equals(feedback, feedback.feedback) && Objects.equals(useAgain, feedback.useAgain);
    }

    public Feedback() {
    }

    public Feedback(String id, String recipientEmail, int rating, String feedback, String useAgain) {
        this.id = id;
        this.recipientEmail = recipientEmail;
        this.rating = rating;
        this.feedback = feedback;
        this.useAgain = useAgain;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientEmail() {
        return this.recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUseAgain() {
        return this.useAgain;
    }

    public void setUseAgain(String useAgain) {
        this.useAgain = useAgain;
    }

    public Feedback id(String id) {
        setId(id);
        return this;
    }

    public Feedback recipientEmail(String recipientEmail) {
        setRecipientEmail(recipientEmail);
        return this;
    }

    public Feedback rating(int rating) {
        setRating(rating);
        return this;
    }

    public Feedback feedback(String feedback) {
        setFeedback(feedback);
        return this;
    }

    public Feedback useAgain(String useAgain) {
        setUseAgain(useAgain);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipientEmail, rating, feedback, useAgain);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", recipientEmail='" + getRecipientEmail() + "'" +
                ", rating='" + getRating() + "'" +
                ", feedback='" + getFeedback() + "'" +
                ", useAgain='" + getUseAgain() + "'" +
                "}";
    }

    // Constructors, getters, and setters
}