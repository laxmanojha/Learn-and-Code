package com.itt.ecommerce.backend;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DemoServlet extends HttpServlet{
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = resp.getWriter();
		UserLogin ul = new UserLogin();
		System.out.println("Name coming in api servlet: " + req.getParameter("name"));
		String result = ul.authenticateUser(req.getParameter("name"));
		out.write(result);
	}
}
