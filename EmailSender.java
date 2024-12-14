package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.HashSet;
import java.util.Set;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailSender {
    private static Properties props;

    public static void sendTestReport(String reportPath, String screenshotsDir) {
        try {
            // Load properties from config file
            props = new Properties();
            props.load(new FileInputStream(System.getProperty("user.dir") + "/src/main/java/config.properties"));

            // Setup mail server properties
            Properties mailProps = new Properties();
            mailProps.put("mail.smtp.auth", props.getProperty("email.smtp.auth"));
            mailProps.put("mail.smtp.starttls.enable", props.getProperty("email.smtp.starttls.enable"));
            mailProps.put("mail.smtp.host", props.getProperty("email.smtp.host"));
            mailProps.put("mail.smtp.port", props.getProperty("email.smtp.port"));

            // Create session with authentication
            Session session = Session.getInstance(mailProps, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            props.getProperty("email.from"),
                            props.getProperty("email.password")
                    );
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(props.getProperty("email.from")));

            // Add recipients
            String[] recipients = props.getProperty("email.recipients").split(",");
            for (String recipient : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.trim()));
            }

            // Set subject with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            message.setSubject("Test Execution Report - " + timestamp);

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Add text body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Please find attached the test execution report and screenshots.\n\n" +
                    "Test execution completed at: " + timestamp);
            multipart.addBodyPart(messageBodyPart);

            // Attach the report
            if (new File(reportPath).exists()) {
                messageBodyPart = new MimeBodyPart();
                ((MimeBodyPart) messageBodyPart).attachFile(new File(reportPath));
                multipart.addBodyPart(messageBodyPart);
                System.out.println("Attached report: " + reportPath);
            }

            // Track test names to avoid duplicate screenshots
            Set<String> processedTests = new HashSet<>();

            // Attach screenshots
            File screenshotsFolder = new File(screenshotsDir);
            if (screenshotsFolder.exists() && screenshotsFolder.isDirectory()) {
                File[] screenshots = screenshotsFolder.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".png"));
                
                if (screenshots != null) {
                    for (File screenshot : screenshots) {
                        // Extract test name from screenshot filename (assuming format: testName_timestamp.png)
                        String testName = screenshot.getName().split("_")[0];
                        
                        // Only attach if we haven't processed this test yet
                        if (!processedTests.contains(testName)) {
                            messageBodyPart = new MimeBodyPart();
                            ((MimeBodyPart) messageBodyPart).attachFile(screenshot);
                            multipart.addBodyPart(messageBodyPart);
                            System.out.println("Attached screenshot: " + screenshot.getName());
                            
                            // Mark this test as processed
                            processedTests.add(testName);
                        } else {
                            System.out.println("Skipping duplicate screenshot: " + screenshot.getName());
                        }
                    }
                }
            }

            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println("Test report email sent successfully to: " + props.getProperty("email.recipients"));

        } catch (Exception e) {
            System.out.println("Failed to send email report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}