package com.itt.ecommerce.backend;

import java.sql.*;

public class UserLogin {

	public String authenticateUser(String inputName) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/week_five_practice", "root", "Rj@1465887732");
            String authenticationResult = "Authentication Failed.";
            try(PreparedStatement ps = con.prepareStatement("Select * from student_detail;");) {
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    String name = result.getString("name");
                    System.out.println("Name in resultset: " + name);
                    if (name.equals(inputName)) {
                    	authenticationResult = "Authentication Success.";
                    	break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return authenticationResult;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
