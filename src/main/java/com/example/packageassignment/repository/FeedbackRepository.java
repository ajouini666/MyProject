package com.example.packageassignment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.packageassignment.model.Feedback;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
}
