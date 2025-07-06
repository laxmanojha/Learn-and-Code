package backend.newsaggregation.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.List;
import java.util.Properties;
import backend.newsaggregation.model.NewsArticle;

public class EmailUtil {

    public static void sendNewsDigestEmail(String toEmail, List<NewsArticle> articles) {
        final String fromEmail = "your_email@example.com"; // your email
        final String password = "your_app_password"; // your app password or real password if allowed

        // SMTP server config
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // for Gmail
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Auth
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        try {
            // Construct the email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, "News Aggregator"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Personalized News Digest");

            StringBuilder content = new StringBuilder();
            content.append("<h2>Here are your personalized news articles:</h2><ul>");

            for (NewsArticle article : articles) {
                content.append("<li><b>")
                       .append(article.getTitle())
                       .append("</b><br>")
                       .append(article.getDescription() != null ? article.getDescription() : "")
                       .append("<br><a href=\"").append(article.getUrl()).append("\">Read More</a></li><br><br>");
            }

            content.append("</ul><br><i>Powered by News Aggregation System</i>");

            // Set content
            message.setContent(content.toString(), "text/html; charset=utf-8");

            // Send message
            Transport.send(message);

            System.out.println("Email sent successfully to " + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
