package myserver;

import java.io.BufferedReader;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;
import stt.Status;

@WebServlet("/studentregister")
public class StudentRegisterServer extends Server {
	private String sex;
	private String university;
	private byte[] file;
	private String major;
	private String expected;

	private String status;
	private static final long serialVersionUID = 1L;
	protected String username;
	protected String password;
	protected String phone;
	protected String address;
	protected String name;
	protected String description;
	protected String date;
	protected DatabaseManagement databaseManagement;
	
	
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
	
	public StudentRegisterServer() {
		// TODO Auto-generated constructor stub
	
        super();
        // TODO Auto-generated constructor stub
}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//getParameterFromRequest(req);
		
		JSONObject json = null;
		String line;
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = req.getReader();
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			String jdata = sb.toString();
			json = (JSONObject) (new JSONParser().parse(jdata));
			System.out.println("json " + json.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//System.out.println(json.toJSONString());
		username = (String) json.get("username");
		password = (String) json.get("password");
		name = (String) json.get("name");
		phone = (String) json.get("phone");
		address = (String) json.get("address");

		date = (String) json.get("birthday");
		university = (String) json.get("university");
		major = (String) json.get("major");
		expected = (String) json.get("expected");
		sex = (String) json.get("sex");
		
		
		databaseManagement = DatabaseManagement.getInstance("root", "hdl123");
		if(isExistUsername()) {
			status = Status.STATUS_EXIST;
		}else {
			insertToUser("student");
			status = Status.STATUS_FAIL;
			Connection connection =  databaseManagement.getConnection();
			String query = createQuery();
			try {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				Calendar calendar = Calendar.getInstance();
				
				java.sql.Date myDate = new java.sql.Date(calendar.getTime().getTime());
				java.text.SimpleDateFormat sdf = 
					     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currentTime = sdf.format(myDate);
				preparedStatement.setString(1, currentTime);
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
				System.out.println(date);
				calendar.setTime(simpleDateFormat.parse(date));
				preparedStatement.setDate(2, new java.sql.Date(calendar.getTime().getTime()));
				preparedStatement.setString(3, sex);
				preparedStatement.setString(4, university);
				preparedStatement.setString(5, major);
				preparedStatement.setString(6, expected);
				preparedStatement.setString(7, username);
				preparedStatement.execute();
				preparedStatement.close();
				status = Status.STATUS_SUCCESS;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			} 
			
			
		}
		sendResponse(resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
		
	}

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		jsonObject = new JSONObject();
		jsonObject.put(Status.STATUS, status);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		try {
			resp.getWriter().write(jsonObject.toJSONString());
			resp.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		// TODO Auto-generated method stub
		username = "sinhvien1";
		password ="1234";
		name ="CTY cua linh";
		phone = "01658924393";
		address = "kontum";
		description ="CTY vip";
		date = "11-02-2018";
		university ="BK";
		sex = "male";
		expected = "pro";
		major ="CS";
		
	}
	

	protected String createQuery() {
		
		String query = " insert into student (timeupdatecv, dateofbirth, sex, university, major, expected, userid)"
		        + " values (?, ?, ?, ?, ?, ?, ?)";
		return query;
	}


}
