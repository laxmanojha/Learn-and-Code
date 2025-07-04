package backend.newsaggregation.model;

import java.util.Date;

public class ExternalServer {
    private int id;
    private String serverName;
    private int serverStatusId;
    private String serverStatus;
    private Date lastAccessed;
    private String apiKey;
    private String apiUrl;

    public ExternalServer() {}

    public ExternalServer(int id, String serverName, String serverStatus, Date lastAccessed, String apiKey) {
        this.id = id;
        this.serverName = serverName;
        this.serverStatus = serverStatus;
        this.lastAccessed = lastAccessed;
        this.apiKey = apiKey;
    }
    
    public ExternalServer(int id, String serverName, String apiUrl, String apiKey, int serverStatusId, Date lastAccessed) {
        this.id = id;
        this.serverName = serverName;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.serverStatusId = serverStatusId;
        this.lastAccessed = lastAccessed;
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

	public String getApiUrl() {
		return apiUrl;
	}

	public int getServerStatusId() {
		return serverStatusId;
	}

	public void setServerStatusId(int serverStatusId) {
		this.serverStatusId = serverStatusId;
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
	
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
}
