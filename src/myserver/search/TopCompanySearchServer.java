package myserver.search;

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

import db.DatabaseManagement;
import key.CompanySearchJSONKey;
import myserver.Server;

public class TopCompanySearchServer extends Server {

	private JSONObject json;
	private JSONArray responseData;
	int max = 10;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getParameterFromRequest(req);
		ResultSet rs = queryData();
		responseData = new JSONArray();
		try {
			while(rs.next()) {
				JSONObject jObj = new JSONObject();
				jObj.put(CompanySearchJSONKey.FULL_NAME, rs.getString(CompanySearchJSONKey.FULL_NAME));
				jObj.put(CompanySearchJSONKey.ADDRESS, rs.getString(CompanySearchJSONKey.ADDRESS));
				jObj.put(CompanySearchJSONKey.DATE_OFF_FOUND, rs.getString(CompanySearchJSONKey.DATE_OFF_FOUND));
				jObj.put(CompanySearchJSONKey.DESCRIPTION, rs.getString(CompanySearchJSONKey.DESCRIPTION));
				jObj.put(CompanySearchJSONKey.KIND_OF_BUSSINESS, rs.getString(CompanySearchJSONKey.KIND_OF_BUSSINESS));
//				jObj.put(CompanySearchJSONKey.NUMBER_OF_VOTE, rs.getString(CompanySearchJSONKey.NUMBER_OF_VOTE));
				jObj.put(CompanySearchJSONKey.PHONE, rs.getString(CompanySearchJSONKey.PHONE));
				jObj.put(CompanySearchJSONKey.RATE, rs.getString(CompanySearchJSONKey.RATE));
				jObj.put(CompanySearchJSONKey.NUM_OF_VIEW, rs.getString(CompanySearchJSONKey.NUM_OF_VIEW));
				responseData.add(jObj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(responseData.toJSONString());
		sendResponse(resp);
	}

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		try {
			resp.getWriter().write(responseData.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {

		
	}
	
	private ResultSet queryData() {
		DatabaseManagement db = DatabaseManagement.getInstance("root", "hdl123");
		Connection connection = db.getConnection();
		ResultSet rs;

		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(
						"SELECT * FROM user,company where username = userid ORDER BY numofview desc LIMIT " + max);
			rs = preparedStatement.executeQuery();
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

	}
	
}
