package com.vkc.consultation.records.adapter.out.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.vkc.consultation.records.application.port.out.EmailPort;

@Component
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledEmailAdapter implements EmailPort {

    private static final Logger logger = LoggerFactory.getLogger(DisabledEmailAdapter.class);

    @Override
    public void sendConsultantWelcomeEmail(String consultantName, String consultantEmail, String temporaryPassword) {
        logger.warn("Email delivery disabled; temporary consultant password for {} not sent.", consultantEmail);
    }
}
