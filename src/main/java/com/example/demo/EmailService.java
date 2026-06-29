package com.example.demo;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    private final JavaMailSender mailSender;
    private InternetAddress internetAddress = new InternetAddress();

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public boolean sendOtp(String toEmail, String code)
    {
        try
        {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your one time code verification!");
            message.setText(code);

            mailSender.send(message);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean isValid(String email)
    {
        try
        {
            internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }
}
