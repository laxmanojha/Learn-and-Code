package backend.newsaggregation.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import backend.newsaggregation.dao.interfaces.ExternalServerDao;

public class ExternalServerDaoImpl implements ExternalServerDao {

    private static ExternalServerDaoImpl instance;

    private ExternalServerDaoImpl() {}

    public static ExternalServerDaoImpl getInstance() {
        if (instance == null) {
            instance = new ExternalServerDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ExternalServer> getAllServersWithStatus() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT id, server_name, server_status, last_accessed FROM external_servers";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ExternalServer server = new ExternalServer();
                server.setId(rs.getInt("id"));
                server.setServerName(rs.getString("server_name"));
                server.setServerStatus(rs.getString("server_status"));
                server.setLastAccessed(rs.getTimestamp("last_accessed"));
                servers.add(server);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    @Override
    public List<ExternalServer> getAllServersWithDetails() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT id, server_name, api_key FROM external_servers";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
        String sql = "SELECT * FROM external_servers WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                server = new ExternalServer();
                server.setId(rs.getInt("id"));
                server.setServerName(rs.getString("server_name"));
                server.setServerStatus(rs.getString("server_status"));
                server.setLastAccessed(rs.getTimestamp("last_accessed"));
                server.setApiKey(rs.getString("api_key"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return server;
    }

    @Override
    public boolean updateApiKey(int id, String newApiKey) {
        String sql = "UPDATE external_servers SET api_key = ? WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newApiKey);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
