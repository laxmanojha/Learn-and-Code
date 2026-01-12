package backend.newsaggregation.util;

import backend.newsaggregation.model.EmailConfig;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.service.EmailConfigService;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.*;

public class EmailUtilTest {

    private EmailConfig mockConfig;
    private List<NewsArticle> mockArticles;
    private Session mockSession;

    @BeforeEach
    public void setup() {
        mockConfig = new EmailConfig();
        mockConfig.setSenderEmail("sender@example.com");
        mockConfig.setAppPassword("dummyPassword");

        NewsArticle article1 = new NewsArticle();
        article1.setTitle("News Title 1");
        article1.setDescription("Description 1");
        article1.setUrl("http://news1.com");

        NewsArticle article2 = new NewsArticle();
        article2.setTitle("News Title 2");
        article2.setDescription(null);
        article2.setUrl("http://news2.com");

        mockArticles = Arrays.asList(article1, article2);
    }

    @Test
    public void testSendNewsDigestEmail_WithValidConfig_ShouldSendEmail() throws Exception {
        try (
            MockedStatic<EmailConfigService> emailConfigServiceMocked = mockStatic(EmailConfigService.class);
            MockedStatic<Transport> transportMocked = mockStatic(Transport.class)
        ) {
            EmailConfigService mockService = mock(EmailConfigService.class);
            emailConfigServiceMocked.when(EmailConfigService::getInstance).thenReturn(mockService);
            when(mockService.getEmailConfig()).thenReturn(mockConfig);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(props);

            MimeMessage message = new MimeMessage(session);
            MimeMessage spyMessage = spy(message);

            EmailUtil.sendNewsDigestEmail("recipient@example.com", mockArticles);

            transportMocked.verify(() -> Transport.send(any(Message.class)));
        }
    }

    @Test
    public void testSendNewsDigestEmail_ConfigIsNull_ShouldLogErrorAndNotSend() {
        try (
            MockedStatic<EmailConfigService> emailConfigServiceMocked = mockStatic(EmailConfigService.class);
            MockedStatic<Transport> transportMocked = mockStatic(Transport.class)
        ) {
            EmailConfigService mockService = mock(EmailConfigService.class);
            emailConfigServiceMocked.when(EmailConfigService::getInstance).thenReturn(mockService);
            when(mockService.getEmailConfig()).thenReturn(null);

            EmailUtil.sendNewsDigestEmail("user@example.com", mockArticles);

            transportMocked.verifyNoInteractions();
        }
    }
}
