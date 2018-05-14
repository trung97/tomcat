package server.myserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import server.db.DatabaseManagement;

public abstract class RegisterServer extends Server{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String username;
	protected String password;
	protected String phone;
	protected String address;
	protected String name;
	protected String description;
	protected String date;
	protected DatabaseManagement databaseManagement;
	
	
	protected abstract String createQuery();
	protected boolean isExistUsername() {
		Connection con = databaseManagement.getConnection();
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select username from user"); 
			while (rs.next()) {
				if ( rs.getString("username").equals(username)) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	protected void insertToUser(String type) {
		String query = " insert into user (username, password, fullname, phone, address, decription, types)"
		        + " values (?, ?, ?, ?, ?, ?, ?)";
		Connection connection =  databaseManagement.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, name);
			preparedStatement.setString(4, phone);
			preparedStatement.setString(5, address);
			preparedStatement.setString(6, description);
			preparedStatement.setString(7, type);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			System.out.println("Them that bai");
			e.printStackTrace();
		}
	}
}
