package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.ExternalServerDao;
import backend.newsaggregation.model.ExternalServer;
import backend.newsaggregation.util.DatabaseConfig;

public class ExternalServerDaoImpl implements ExternalServerDao {

    private static ExternalServerDaoImpl instance;
    Connection conn = DatabaseConfig.getConnection();

    private ExternalServerDaoImpl() {}

    public static ExternalServerDaoImpl getInstance() {
        if (instance == null) {
            instance = new ExternalServerDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ExternalServer> getAllServersBasicDetails() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT e.id, e.server_name, s.type, e.last_accessed FROM external_server e INNER JOIN"
        		+ " server_status s ON e.server_status_id = s.id";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExternalServer server = new ExternalServer();
                server.setId(rs.getInt("e.id"));
                server.setServerName(rs.getString("e.server_name"));
                server.setServerStatus(rs.getString("s.type"));
                server.setLastAccessed(rs.getTimestamp("e.last_accessed"));
                servers.add(server);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    @Override
    public List<ExternalServer> getAllServersWithApiKeys() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT id, server_name, api_key FROM external_server";

        try {
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExternalServer server = new ExternalServer();
                server.setId(rs.getInt("id"));
                server.setServerName(rs.getString("server_name"));
                server.setApiKey(rs.getString("api_key"));
                servers.add(server);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    @Override
    public ExternalServer getServerById(int id) {
        ExternalServer server = null;
        String sql = "SELECT * FROM external_server e INNER JOIN server_status s ON e.server_status_id = s.id WHERE e.id = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                server = new ExternalServer();
                server.setId(rs.getInt("e.id"));
                server.setServerName(rs.getString("e.server_name"));
                server.setServerStatus(rs.getString("s.type"));
                server.setLastAccessed(rs.getTimestamp("e.last_accessed"));
                server.setApiKey(rs.getString("e.api_key"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return server;
    }

    @Override
    public boolean updateApiKey(int id, String newApiKey) {
        String sql = "UPDATE external_server SET api_key = ? WHERE id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newApiKey);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    @Override
    public boolean updateServerStatusAndLastAccess(int serverId, int statusId) {
        String sql = """
            UPDATE external_server
            SET server_status_id = ?, last_accessed = NOW()
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, serverId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ExternalServer getServerByName(String serverName) {
        String sql = "SELECT * FROM external_server WHERE server_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serverName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ExternalServer server = new ExternalServer();
                server.setId(rs.getInt("id"));
                server.setServerName(rs.getString("server_name"));
                server.setApiUrl(rs.getString("api_url"));
                server.setApiKey(rs.getString("api_key"));
                server.setServerStatusId(rs.getInt("server_status_id"));
                server.setLastAccessed(rs.getTimestamp("last_accessed"));
                return server;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
