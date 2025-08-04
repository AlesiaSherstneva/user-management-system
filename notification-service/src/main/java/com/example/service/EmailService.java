package com.example.service;

import com.example.dao.AdminEmailDao;
import com.example.kafka.event.UserEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final AdminEmailDao adminEmailDao;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendAdminNotification(UserEvent event) {
        log.info("Preparing notification process");
        List<String> adminEmails = adminEmailDao.findAdminEmails();

        if (adminEmails.isEmpty()) {
            log.warn("No admin emails found. Notification won't be sent");
        } else {
            log.info("Found {} admin emails for notification", adminEmails.size());
        }

        try {
            Context ctx = new Context();
            ctx.setVariable("event", event);

            String htmlContent = templateEngine.process("notification", ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@user-management.com");
            helper.setTo(adminEmails.toArray(String[]::new));
            helper.setSubject(String.format("%s пользователь %s", event.getAction().getInRussian(), event.getUserName()));
            helper.setText(htmlContent, true);
            log.debug("Email message constructed");

            mailSender.send(message);
            log.info("Notification sent to {} admins", adminEmails.size());
        } catch (Exception ex) {
            log.error("Failed to process event: {}", event, ex);
        }
    }
}