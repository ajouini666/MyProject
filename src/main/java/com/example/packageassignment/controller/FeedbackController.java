package com.example.packageassignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.packageassignment.model.Feedback;
import com.example.packageassignment.repository.FeedbackRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
public class FeedbackController {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/feedback")
    public ResponseEntity<String> submitFeedback(@RequestBody Feedback feedback) {
        try {
            feedbackRepository.save(feedback);
            return new ResponseEntity<>("Feedback submitted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to submit feedback.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
