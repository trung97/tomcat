package myserver;

import java.io.BufferedReader;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import db.DatabaseManagement;
import stt.Status;


@WebServlet("/companyregister")
public class CompanyRegisterServer extends Server {

	/**
	 * 
	 */
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
	private static final long serialVersionUID = 1L;
	private int serialNumber;
	private String kind;
	private String status;
	public CompanyRegisterServer() {
        super();
        // TODO Auto-generated constructor stub
}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// getParameterFromRequest(req);

				JSONObject json = null;
				String line;
				StringBuffer sb = new StringBuffer();
				try {
					BufferedReader br = req.getReader();
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}

					String jdata = sb.toString();
					json = (JSONObject) (new JSONParser().parse(jdata));
					System.out.println("json: " + json.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				username = (String) json.get("username");
				password = (String) json.get("password");
				name = (String) json.get("name");
				phone = (String) json.get("phone");
				address = (String) json.get("address");

				date = (String) json.get("datefoundation");
				String serial = (String) json.get("series");
				serialNumber = Integer.valueOf(serial);
				kind = (String) json.get("field");
				//wtf
				databaseManagement = DatabaseManagement.getInstance("root", "hdl123");
				if (isExistUsername()) {
					status = Status.STATUS_EXIST;
				} else {
					insertToUser("doanhnghiep");
					status = Status.STATUS_FAIL;
					Connection connection = databaseManagement.getConnection();

					try {

						String query = createQuery();
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, serialNumber);

						Calendar calendar = Calendar.getInstance();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
						calendar.setTime(simpleDateFormat.parse(date));
						preparedStatement.setDate(2, new java.sql.Date(calendar.getTime().getTime()));
						preparedStatement.setFloat(3, 0);
						preparedStatement.setInt(4, 0);
						preparedStatement.setString(5, kind);
						preparedStatement.setString(6, username);
						preparedStatement.execute();
						preparedStatement.close();
						status = Status.STATUS_SUCCESS;

					} catch (SQLException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				sendResponse(resp);
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

	}

	
	protected String createQuery() {

		String query = " insert into company (seriesnumber, dateoffound, rate, numberofvoters, kindofbusiness,userid)"
				+ " values (?, ?, ?, ?, ?, ?)";
		return query;
	}

}
