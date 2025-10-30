package com.ems.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  public void sendPasswordSetupEmail(String toEmail, String firstName, String lastName, String employeeId) {
    try {

      String emailBody = String.format("""
        Dear %s %s,
        
        Welcome to our company!
        
        Your employee account has been created successfully.
        Employee ID: %s
        
        To access your account, kindly login with your email and password: password123
        You're advised to change your password immediately for security purposes.
        
        This link will expire in 24 hours.
        
        Best regards,
        HR Team
        """, firstName, lastName, employeeId);

      log.info("Email to be sent: {}", emailBody);
    } catch (Exception e) {
      log.error("Failed to send email to: {}. Error: {}", toEmail, e.getMessage());
    }
  }
}
