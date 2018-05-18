package myserver;

import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import db.DatabaseManagement;
import java.util.Date;


public class StudentAccountUpdateServer extends AccountUpdateServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sex;
	private String university;
	private String CV;
	private String major;
	private String expected;
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getParameterFromRequest(req);
		Connection con =  DatabaseManagement.getInstance("root","").getConnection();
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(stmtUpdateUser());
			stmt.executeUpdate(stmtUpdateStudent());
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
		String json="{\"username\":\"nguyenvana@gmail.com\",\"fullname\":{\"flagChanged\":\"1\",\"data\":\"Localhost\"},\"password\":{\"flagChanged\":\"1\",\"data\":\"123\"},\"sex\":{\"flagChanged\":\"1\",\"data\":\"male\"}}";
		JSONParser parser = new JSONParser();
		System.out.println(json);
		try {
			jsonObject= (JSONObject)parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	protected String stmtUpdateUser() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("UPDATE user SET ");
		if (checkChanged("password")) buffer.append("password=\""+getData("password")+"\"");
		if (checkChanged("fullname")) buffer.append(",fullname=\""+getData("fullname")+"\"");
		//if (checkChanged("address")) buffer.append(",address=\""+getData("address")+"\"");
		//if (checkChanged("decription")) buffer.append(",decription=\""+getData("decription")+"\"");
		buffer.append("WHERE username= \"" + jsonObject.get("username").toString() +"\";" );
		return buffer.toString();
	}
	private String stmtUpdateStudent() {
		StringBuffer buffer=new StringBuffer();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		buffer.append("UPDATE student SET ");
		if (checkChanged("sex")) buffer.append("timeupdatecv=\""+dateFormat.format(date)+"\"");
		//if (checkChanged("dateofbirth")) buffer.append(",dateofbirth=\""+getData("dateofbirth")+"\"");
		if (checkChanged("sex")) buffer.append(",sex=\""+getData("sex")+"\"");
		//if (checkChanged("university")) buffer.append(",university=\""+getData("university")+"\"");
		//if (checkChanged("major")) buffer.append(",major=\""+getData("major")+"\"");
		//if (checkChanged("cv")) buffer.append(",cv=\""+getData("cv")+"\"");
		//if (checkChanged("expected")) buffer.append(",expected=\""+getData("expected")+"\"");
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
