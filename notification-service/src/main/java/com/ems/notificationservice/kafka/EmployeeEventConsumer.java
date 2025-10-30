package com.ems.notificationservice.kafka;

import com.ems.notificationservice.event.EmployeeCreatedEvent;
import com.ems.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeEventConsumer {

  private final EmailService emailService;

  @KafkaListener(
          topics = "${kafka.topic.employee-created}",
          groupId = "${spring.kafka.consumer.group-id}"
  )
  public void consumeEmployeeCreatedEvent(EmployeeCreatedEvent event) {
    log.info("Received employee created event for employee: {}", event.getEmployeeId());
    
    try {
      // Send password setup email to the new employee
      emailService.sendPasswordSetupEmail(
          event.getEmail(),
          event.getFirstName(),
          event.getLastName(),
          event.getEmployeeId().toString()
      );
      
      log.info("Successfully processed employee created event for: {}", event.getEmail());
    } catch (Exception e) {
      log.error("Error processing employee created event: {}", e.getMessage(), e);
    }
  }
}
