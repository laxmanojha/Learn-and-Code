package backend.newsaggregation.model;

public class NotificationPref {
    private int userId;
    private boolean notifyBusiness;
    private boolean notifyEntertainment;
    private boolean notifySports;
    private boolean notifyTechnology;
    private String keywords;

    public NotificationPref() {}

    public NotificationPref(int userId, boolean notifyBusiness, boolean notifyEntertainment, boolean notifySports, boolean notifyTechnology, String keywords) {
        this.userId = userId;
        this.notifyBusiness = notifyBusiness;
        this.notifyEntertainment = notifyEntertainment;
        this.notifySports = notifySports;
        this.notifyTechnology = notifyTechnology;
        this.keywords = keywords;
    }

	public int getUserId() {
		return userId;
	}

	public boolean isNotifyBusiness() {
		return notifyBusiness;
	}

	public boolean isNotifyEntertainment() {
		return notifyEntertainment;
	}

	public boolean isNotifySports() {
		return notifySports;
	}

	public boolean isNotifyTechnology() {
		return notifyTechnology;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setNotifyBusiness(boolean notifyBusiness) {
		this.notifyBusiness = notifyBusiness;
	}

	public void setNotifyEntertainment(boolean notifyEntertainment) {
		this.notifyEntertainment = notifyEntertainment;
	}

	public void setNotifySports(boolean notifySports) {
		this.notifySports = notifySports;
	}

	public void setNotifyTechnology(boolean notifyTechnology) {
		this.notifyTechnology = notifyTechnology;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
