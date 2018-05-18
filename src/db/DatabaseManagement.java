package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManagement {
	private static DatabaseManagement instance = null;
	private Connection con;
	private String username;
	private String password;
	public static DatabaseManagement getInstance(String username,String password) {
		if(instance==null) {
			instance = new DatabaseManagement(username,password);
		}
		return instance;
	}
	private DatabaseManagement(String username,String password){
		this.username = username;
		this.password = password;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + "jws-app-mysql" + ":" + "3306" + "/"
			+ "lllc","user","password");
			username = ""; password ="";
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return this.con;
	}
	
	public static void main(String[] args) {
		DatabaseManagement db = DatabaseManagement.getInstance("user", "password");
	}
}
