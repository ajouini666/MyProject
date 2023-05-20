package com.example.packageassignment.EmailSender.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.packageassignment.EmailSender.model.EmployeeSignup;
import com.example.packageassignment.service.SecurityEncryption;

@RequestMapping("/api/SignupPermission")
@RestController
public class SignupPermissionSender {

    @Autowired
    SecurityEncryption SecurityEncription;

    @Autowired
    JavaMailSender javaMailSender;

    @PostMapping("/sendMail")
    public ResponseEntity<Object> sendEmail(@RequestBody EmployeeSignup EmployeeSignup) {

        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setFrom("ajouini666@gmail.com");// input the sender email Id or read it from properties file
        sm.setTo(EmployeeSignup.getEmail());
        sm.setSubject("Love you so much");
        sm.setText(
                "Welcome to our Team! " + EmployeeSignup.getName() + "\n\n Here's your signup code:" + "\n \n"
                        + SecurityEncription.generateCode(EmployeeSignup.getRole())
                        + "\n\n Note it is a one time only Signup code you will need it after you create your account.");
        javaMailSender.send(sm);

        return generateResponse("Email Sent to the mail " + EmployeeSignup.getEmail(), HttpStatus.OK,
                EmployeeSignup.getEmail());
    }

    public ResponseEntity<Object> generateResponse(String msg, HttpStatus st, Object response) {
        Map<String, Object> mp = new HashMap<String, Object>();

        mp.put("message", msg);
        mp.put("status", st.value());
        mp.put("data", response);

        return new ResponseEntity<Object>(mp, st);
    }

}
