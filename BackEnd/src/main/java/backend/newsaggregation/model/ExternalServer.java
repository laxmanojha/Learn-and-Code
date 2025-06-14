package backend.newsaggregation.model;

import java.util.Date;

public class ExternalServer {
    private int id;
    private String serverName;
    private String serverStatus;
    private Date lastAccessed;
    private String apiKey;

    public ExternalServer() {}

    public ExternalServer(int id, String serverName, String serverStatus, Date lastAccessed, String apiKey) {
        this.id = id;
        this.serverName = serverName;
        this.serverStatus = serverStatus;
        this.lastAccessed = lastAccessed;
        this.apiKey = apiKey;
    }

	public int getId() {
		return id;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerStatus() {
		return serverStatus;
	}

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerStatus(String serverStatus) {
		this.serverStatus = serverStatus;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
