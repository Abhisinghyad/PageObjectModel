package project;

import java.io.File;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class Email {

    public static void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath) {

        final String fromEmail = "styloabhisheksingh0@gmail.com"; // Sender email
        final String password = "nvgs gcwo ihvh rszw";      // App password or real password

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        properties.put("mail.smtp.port", "587"); // TLS Port
        properties.put("mail.smtp.auth", "true"); // Enable Authentication
        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

        // Create Session
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);

            // Create Body Part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Attachment Part
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentPath);
            messageBodyPart.setDataHandler(new DataHandler(source));
       
            messageBodyPart.setFileName("LoginTestReport.html");
            multipart.addBodyPart(messageBodyPart);

            // Send the full message
            message.setContent(multipart);

            // Send Message
            Transport.send(message);

            System.out.println("Test Report Sent Successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
