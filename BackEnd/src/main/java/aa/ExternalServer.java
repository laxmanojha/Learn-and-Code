package aa;

import java.util.Date;

public class ExternalServer {
    private int id;
    private String serverName;
    private String apiUrl;
    private String apiKey;
    private int serverStatusId;
    private Date lastAccessed;
	public int getId() {
		return id;
	}
	public String getServerName() {
		return serverName;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public String getApiKey() {
		return apiKey;
	}
	public int getServerStatusId() {
		return serverStatusId;
	}
	public Date getLastAccessed() {
		return lastAccessed;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public void setServerStatusId(int serverStatusId) {
		this.serverStatusId = serverStatusId;
	}
	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

    
}
