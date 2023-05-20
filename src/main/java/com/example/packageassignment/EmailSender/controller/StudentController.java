package com.example.packageassignment.EmailSender.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.packageassignment.EmailSender.model.Student;

import jakarta.mail.internet.MimeMessage;

@RequestMapping("/api")
@RestController
public class StudentController {
	@Autowired
	JavaMailSender javaMailSender;

	@PostMapping("/sendMail")
	public ResponseEntity<Object> sendEmail(@RequestBody Student student) {

		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setFrom("ajouini666@gmail.com");// input the sender email Id or read it from properties file
		sm.setTo(student.getEmail());
		sm.setSubject("Love you so much");
		sm.setText(
				"Hello " + student.getName() + "\n\n this email was sent via springboot, I DID IT HHHHHHHHHHHHHHHHHHH");
		javaMailSender.send(sm);

		return generateResponse("Email Sent to the mail " + student.getEmail(), HttpStatus.OK, student);
	}

	public ResponseEntity<Object> generateResponse(String msg, HttpStatus st, Object response) {
		Map<String, Object> mp = new HashMap<String, Object>();

		mp.put("message", msg);
		mp.put("status", st.value());
		mp.put("data", response);

		return new ResponseEntity<Object>(mp, st);
	}

	@PostMapping("/sendMailTemp")
	public ResponseEntity<String> sendEmailFromTemplate() {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("ajouini666@gmail.com");
			helper.setTo("ajouini2001@gmail.com");
			helper.setSubject("Test email from my Spring application");

			// Read the HTML template from the classpath
			InputStream inputStream = getClass().getResourceAsStream("/templates/survey-email.html");
			String htmlTemplate = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

			// Set the email's content to be the HTML template
			helper.setText(htmlTemplate, true);

			javaMailSender.send(message);

			// Email sending success
			return ResponseEntity.ok("Email sent successfully");
		} catch (Exception e) {
			// Log the exception
			e.printStackTrace();

			// Handle the exception or throw it to propagate the error
			// You can customize the error handling based on your requirements

			// Email sending failure
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
		}
	}

}
