package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.EmailConfigDao;
import backend.newsaggregation.model.EmailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailConfigServiceTest {

    @Mock
    private EmailConfigDao emailConfigDao;

    @InjectMocks
    private EmailConfigService emailConfigService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        emailConfigService = new EmailConfigService(emailConfigDao);
    }

    @Test
    public void testGetEmailConfig() {
        EmailConfig config = new EmailConfig();
        config.setSenderEmail("test@example.com");
        config.setAppPassword("secret");

        when(emailConfigDao.getEmailConfig()).thenReturn(config);

        EmailConfig result = emailConfigService.getEmailConfig();

        assertNotNull(result);
        assertEquals("test@example.com", result.getSenderEmail());
        assertEquals("secret", result.getAppPassword());
        verify(emailConfigDao).getEmailConfig();
    }

    @Test
    public void testGetEmailConfig_ReturnsNull() {
        when(emailConfigDao.getEmailConfig()).thenReturn(null);

        EmailConfig result = emailConfigService.getEmailConfig();

        assertNull(result);
        verify(emailConfigDao).getEmailConfig();
    }

    @Test
    public void testSingletonInstance_NotNull() {
        EmailConfigService instance = EmailConfigService.getInstance();
        assertNotNull(instance);
    }
}
