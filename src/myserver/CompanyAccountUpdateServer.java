package myserver;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;

public class CompanyAccountUpdateServer extends Server{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int serialNumber;
	private String dateOfFound;
	private float rate;
	private int numOfVote;
	private String kindOfBusiness;
	private String userID;

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getParameterFromRequest(req);
		Connection con =  DatabaseManagement.getInstance("root","123456").getConnection();
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(stmtUpdateUser());
			stmt.executeUpdate(stmtUpdateCompany());
			//con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void sendResponse(HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		String json="{\"username\":\"lol@gmail.com\",\"fullname\":{\"flagChanged\":\"1\",\"data\":\"gg\"},\"password\":{\"flagChanged\":\"1\",\"data\":\"123\"},\"kindofbusiness\":{\"flagChanged\":\"1\",\"data\":\"Teachnology\"}}";
		JSONParser parser = new JSONParser();
		try {
			jsonObject= (JSONObject)parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String stmtUpdateUser() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("UPDATE user SET ");
		if (checkChanged("password")) buffer.append("password=\""+getData("password")+"\"");
		if (checkChanged("fullname")) buffer.append(",fullname=\""+getData("fullname")+"\"");
		//if (checkChanged("address")) buffer.append(",address=\""+getData("address")+"\"");
		//if (checkChanged("decription")) buffer.append(",decription=\""+getData("decription")+"\"");
		buffer.append("WHERE username= \"" + jsonObject.get("username").toString() +"\";" );
		return buffer.toString();
	}
	private String stmtUpdateCompany() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("UPDATE company SET ");
		//if (checkChanged("seriesnumber")) buffer.append("seriesnumber=\""+getData("seriesnumber")+"\"");
		//if (checkChanged("dateoffound")) buffer.append(",dateoffound=\""+getData("dateoffound")+"\"");
		if (checkChanged("kindofbusiness")) buffer.append("kindofbusiness=\""+getData("kindofbusiness")+"\"");
		buffer.append("WHERE userid= \"" + jsonObject.get("username").toString() +"\";" );
		return buffer.toString();
	}
	private boolean checkChanged( String key) {
		JSONObject data= (JSONObject) jsonObject.get(key);
		if (data.get("flagChanged").toString().equals("0")) return false;
		else return true;
	} 
	private String getData( String key) {
		JSONObject data= (JSONObject) jsonObject.get(key);
		return data.get("data").toString();
	} 
}
