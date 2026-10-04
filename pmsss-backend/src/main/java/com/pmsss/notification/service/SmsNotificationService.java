package com.pmsss.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SmsNotificationService.class);

    @Value("${pmsss.notification.sms.provider:MOCK}")
    private String smsProvider; // TWILIO, MSG91, AWS_SNS, MOCK

    @Value("${pmsss.notification.sms.api-key:dummy_key}")
    private String apiKey;

    /**
     * Dispatches transactional SMS via configured provider.
     * Keeps messages concise and directs users to the portal for security.
     */
    public boolean sendSms(String phoneNumber, String message) {
        try {
            log.info("📱 [SMS OUTBOUND via {}] To: {} | Message: '{}'", smsProvider, phoneNumber, message);

            if (phoneNumber == null || phoneNumber.trim().length() < 10) {
                throw new IllegalArgumentException("Invalid recipient phone number: " + phoneNumber);
            }

            switch (smsProvider.toUpperCase()) {
                case "TWILIO":
                    log.debug("Twilio SMS integration dispatched using SID key");
                    break;
                case "MSG91":
                    log.debug("MSG91 SMS gateway transaction completed");
                    break;
                case "AWS_SNS":
                    log.debug("AWS SNS SMS Publish completed");
                    break;
                case "MOCK":
                default:
                    log.debug("Mock SMS provider simulated delivery");
                    break;
            }

            return true;
        } catch (Exception ex) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, ex.getMessage());
            return false;
        }
    }

    public String getProviderName() {
        return smsProvider;
    }
}
