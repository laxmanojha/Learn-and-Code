package com.itt.ecommerce.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.itt.ecommerce.constants.StaticConfigurations;

public class DatabaseConfig {
    private static Connection connection = null;
    private static final int MAKING_CONNECTION_LIMIT = 5;

    private DatabaseConfig() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
    	String applicationPropPath = StaticConfigurations.APPLICATION_PROP_PATH;
    	Properties dbCredentials = new Properties();
    	try(InputStream input = DatabaseConfig.class.getResourceAsStream(applicationPropPath)) {
    		dbCredentials.load(input);
    		for(int index = 0; index < MAKING_CONNECTION_LIMIT; index++) {
    			if (connection == null) {
    				Class.forName((String) dbCredentials.get("className"));
    				connection = DriverManager.getConnection((String) dbCredentials.get("url"), (String) dbCredentials.get("user"), (String) dbCredentials.get("mysqlPassword"));
    			} else {
					break;
				}
    		}
    	} catch (SQLException | ClassNotFoundException | IOException e) {
    		System.out.println(e.getMessage());
    	}
    	return connection;
    }
}
