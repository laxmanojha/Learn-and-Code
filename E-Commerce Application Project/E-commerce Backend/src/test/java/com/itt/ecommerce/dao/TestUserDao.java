package com.itt.ecommerce.dao;

import com.itt.ecommerce.dto.UserDto;
import com.itt.ecommerce.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestUserDao {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockPreparedStatement;
    @Mock ResultSet mockResultSet;

    @InjectMocks UserDao userDao;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        DatabaseConfig.setConnection(mockConnection);
        userDao = new UserDao();
    }

    @Test
    public void testAddUser_Success() throws Exception {
        UserDto user = new UserDto("Test User", "testuser", "testpass");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userDao.addUser(user);
        assertTrue(result);

        verify(mockPreparedStatement, times(1)).setString(1, user.getFullName());
        verify(mockPreparedStatement, times(1)).setString(2, user.getUserName());
        verify(mockPreparedStatement, times(1)).setString(3, user.getPassword());
    }

    @Test
    public void testGetUserBasedOnUserCredentials_Found() throws Exception {
        UserDto user = new UserDto("Test User", "testuser", "testpass");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getString("full_name")).thenReturn("Test User");
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockResultSet.getString("password")).thenReturn("testpass");

        UserDto result = userDao.getUserBasedOnUserCredentials(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
    }

    @Test
    public void testGetUserBasedOnUserCredentials_NotFound() throws Exception {
        UserDto user = new UserDto("Test User", "nonexistent", "pass");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        UserDto result = userDao.getUserBasedOnUserCredentials(user);

        assertNull(result);
    }

    @Test
    public void testGetUserBasedOnUsername_Found() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("user_id")).thenReturn(2);
        when(mockResultSet.getString("full_name")).thenReturn("Test User");
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockResultSet.getString("password")).thenReturn("testpass");

        UserDto result = userDao.getUserBasedOnUsername("testuser");

        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
    }

    @Test
    public void testGetUserIDByUsername_Found() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("user_id")).thenReturn(10);

        int userId = userDao.getUserIDByUsername("testuser");

        assertEquals(10, userId);
    }

    @Test
    public void testGetUserIDByUsername_NotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        int userId = userDao.getUserIDByUsername("missinguser");

        assertEquals(-1, userId);
    }
}
