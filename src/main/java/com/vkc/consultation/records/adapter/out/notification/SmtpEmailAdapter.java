package com.vkc.consultation.records.adapter.out.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.vkc.consultation.records.application.port.out.EmailPort;

@Component
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
public class SmtpEmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpEmailAdapter(
            JavaMailSender mailSender,
            @Value("${app.mail.from:no-reply@consultation-records.local}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendConsultantWelcomeEmail(String consultantName, String consultantEmail, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(consultantEmail);
        message.setSubject("Your consultant account temporary password");
        message.setText(buildWelcomeBody(consultantName, temporaryPassword));
        mailSender.send(message);
    }

    private String buildWelcomeBody(String consultantName, String temporaryPassword) {
        return "Hello " + (consultantName == null || consultantName.isBlank() ? "Consultant" : consultantName) + ",\n\n"
                + "Your consultant account has been created.\n"
                + "Temporary password: " + temporaryPassword + "\n\n"
                + "Please sign in and change your password immediately.\n\n"
                + "Regards,\nConsultation Records Team";
    }
}
