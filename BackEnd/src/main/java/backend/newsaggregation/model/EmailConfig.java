package backend.newsaggregation.model;


public class EmailConfig {
    private int id;
    private String senderEmail;
    private String appPassword;
    private String displayName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getAppPassword() { return appPassword; }
    public void setAppPassword(String appPassword) { this.appPassword = appPassword; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

