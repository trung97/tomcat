package search;

import java.io.BufferedReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;
import key.CompanySearchJSONKey;
import myserver.Server;


@WebServlet("/companysearch")
public class CompanySearchServer extends Server {
	
	
	
	public CompanySearchServer() {
        super();
        // TODO Auto-generated constructor stub
    }
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json =null;
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
			System.out.println(jdata);
			json = (JSONObject) (new JSONParser().parse(jdata));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResultSet rs = queryDataBySimpleField(json);
		responseData = new JSONArray();
		DatabaseManagement db = DatabaseManagement.getInstance("root", "123456");
		Connection connection = db.getConnection();
		try {
			while(rs.next()) {
				
				String idCompany = rs.getString("username");
				System.out.println(idCompany);
				System.out.println(json.get("id")); 
				ResultSet rs1;
				PreparedStatement preparedStatement = connection.prepareStatement("select * from follower where useridstudent = ? and useridcompany = ?");
				preparedStatement.setString(1,(String) json.get("id"));
				preparedStatement.setString(2,idCompany);
				rs1 = preparedStatement.executeQuery();
				JSONObject jObj = new JSONObject();
				if(rs1.next()) {
					jObj.put("follow", "yes");
				}else {
					jObj.put("follow", "no");
				}
				
				
				
				jObj.put("id", rs.getString("username"));
				jObj.put(CompanySearchJSONKey.FULL_NAME, rs.getString(CompanySearchJSONKey.FULL_NAME));
				jObj.put(CompanySearchJSONKey.ADDRESS, rs.getString(CompanySearchJSONKey.ADDRESS));
				jObj.put(CompanySearchJSONKey.DATE_OFF_FOUND, rs.getString(CompanySearchJSONKey.DATE_OFF_FOUND));
				jObj.put(CompanySearchJSONKey.DESCRIPTION, rs.getString(CompanySearchJSONKey.DESCRIPTION));
				jObj.put(CompanySearchJSONKey.KIND_OF_BUSSINESS, rs.getString(CompanySearchJSONKey.KIND_OF_BUSSINESS));
//				jObj.put(CompanySearchJSONKey.NUMBER_OF_VOTE, rs.getString(CompanySearchJSONKey.NUMBER_OF_VOTE));
				jObj.put(CompanySearchJSONKey.PHONE, rs.getString(CompanySearchJSONKey.PHONE));
				jObj.put(CompanySearchJSONKey.RATE, rs.getString(CompanySearchJSONKey.RATE));
				
				responseData.add(jObj);
				
			}
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			System.out.println(responseData.toJSONString());
			resp.getWriter().println(responseData);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		
		
	}
	
	private ResultSet queryDataBySimpleField(JSONObject json) { //name,address, kindofbussiness
		DatabaseManagement db = DatabaseManagement.getInstance("root", "123456");
		Connection connection = db.getConnection();
		ResultSet rs;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select * from company, user"
					+ " where username = userid and fullname like ? and address like ?"
					+ " and kindofbusiness like ?");
			preparedStatement.setString(1,"%" + (String) json.get(CompanySearchJSONKey.FULL_NAME) + "%");
			preparedStatement.setString(2,"%" + (String) json.get(CompanySearchJSONKey.ADDRESS) + "%");
			preparedStatement.setString(3,"%" + (String) json.get(CompanySearchJSONKey.KIND_OF_BUSSINESS) + "%");
			rs = preparedStatement.executeQuery();
		return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		//columnNameAndValues = new ArrayList<>();
		
		
	}

}
