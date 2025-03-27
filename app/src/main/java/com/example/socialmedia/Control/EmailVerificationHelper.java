package com.example.socialmedia.Control;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailVerificationHelper {

    private static final String FROM_EMAIL = "yourEmail@gmail.com"; // Email used to send code verification
    private static final String APP_PASSWORD = "password email";  //  App Password
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  //100000<=code<=999999 code length is 6
        return String.valueOf(code);
    }

     public void sendVerificationEmail(String userEmail, String verificationCode, EmailCallback callback) {
         executorService.execute(() -> {
            try {
                 Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.gmail.com");  
                properties.put("mail.smtp.port", "587");  
                properties.put("mail.smtp.auth", "true");   
                properties.put("mail.smtp.starttls.enable", "true"); 
                properties.put("mail.smtp.connectiontimeout", "5000"); 
                properties.put("mail.smtp.timeout", "5000");        
                properties.put("mail.smtp.writetimeout", "5000");      

                 Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD); 
                    }
                });

                 Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FROM_EMAIL));  
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); 
                message.setSubject("The verification code");  
                message.setText("Your code is: " + verificationCode); 

                 try {
                    Transport.send(message);  
                    callback.onSuccess();  
                } catch (MessagingException e) {
                     callback.onFailure(e);  
                }
            } catch (MessagingException e) {
                 callback.onFailure(e);  
            }
        });
    }


     public String initiateEmailVerification(String userEmail, EmailCallback callback) {
        String verificationCode = generateVerificationCode();
        sendVerificationEmail(userEmail, verificationCode, callback);
        return verificationCode;
    }

     public interface EmailCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
