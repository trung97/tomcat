package myserver.search;

import java.io.BufferedReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;
import key.CompanySearchJSONKey;
import key.StudentSearchJSONKey;
import myserver.Server;

public class StudentSearchServer extends Server {

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = null;
		JSONArray responseData;
		
		String line;
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = req.getReader();
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			String jdata = sb.toString();
			jdata = jdata.substring(1, jdata.length() - 1);
			json = (JSONObject) (new JSONParser().parse(jdata));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ResultSet rs = queryDataBySimpleField(json);
		responseData = new JSONArray();
		
		try {
			while(rs.next()) {
				JSONObject jObj = new JSONObject();
				jObj.put("id", rs.getString("username"));
				jObj.put(StudentSearchJSONKey.FULL_NAME, rs.getString(StudentSearchJSONKey.FULL_NAME));
				jObj.put(StudentSearchJSONKey.PHONE, rs.getString(StudentSearchJSONKey.PHONE));
				jObj.put(StudentSearchJSONKey.ADDRESS, rs.getString(StudentSearchJSONKey.ADDRESS));
				jObj.put(StudentSearchJSONKey.DESCRIPTION, rs.getString(StudentSearchJSONKey.DESCRIPTION));
				jObj.put(StudentSearchJSONKey.DATE_OF_BIRTH, rs.getString(StudentSearchJSONKey.DATE_OF_BIRTH));
				jObj.put(StudentSearchJSONKey.SEX, rs.getString(StudentSearchJSONKey.SEX));
				jObj.put(StudentSearchJSONKey.UNIVERSITY, rs.getString(StudentSearchJSONKey.UNIVERSITY));
				jObj.put(StudentSearchJSONKey.MAJOR, rs.getString(StudentSearchJSONKey.MAJOR));
				jObj.put(StudentSearchJSONKey.EXPECTED, rs.getString(StudentSearchJSONKey.EXPECTED));
				responseData.add(jObj);
			}
			
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(responseData.toJSONString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private ResultSet queryDataBySimpleField(JSONObject json) { //name,address, kindofbussiness // chut sua
		DatabaseManagement db = DatabaseManagement.getInstance("root", "hdl123");
		Connection connection = db.getConnection();
		ResultSet rs;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select * from student, user"
					+ " where username = userid and  university like ? and address like ?"
					+ " and major like ?");
			preparedStatement.setString(1,"%" + (String) json.get(StudentSearchJSONKey.UNIVERSITY) + "%");
			preparedStatement.setString(2,"%" + (String) json.get(StudentSearchJSONKey.ADDRESS) + "%");
			preparedStatement.setString(3,"%" + (String) json.get(StudentSearchJSONKey.MAJOR) + "%");
			rs = preparedStatement.executeQuery();
		return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	

}
