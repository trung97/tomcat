package myserver;

import java.io.BufferedReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;
import stt.Status;

/**
 * Servlet implementation class FollowServer
 */
@WebServlet("/FollowServer")
public class FollowServer extends Server {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see Server#Server()
     */
    public FollowServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		getParameterFromRequest(req);
		System.out.println(jsonObject.toJSONString());
		
		String useridstudent = jsonObject.get("useridstudent").toString();
		String useridcompany = jsonObject.get("useridcompany").toString();
		Connection con = DatabaseManagement.getInstance("root", "123456").getConnection();
		try {
			if (checkFollowingYet(useridstudent, useridcompany)) {
				// exist meaning delete follow
				String SQL = "DELETE FROM follower WHERE useridstudent = ? and useridcompany=? ";
				PreparedStatement pstmt = null;
				pstmt = con.prepareStatement(SQL);
				pstmt.setString(1, useridstudent);
				pstmt.setString(2, useridcompany);
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				// insert new
				String query = " insert into follower (useridstudent,useridcompany)  values (?, ?)";
				PreparedStatement preparedStatement = con.prepareStatement(query);
				preparedStatement.setString(1, useridstudent);
				preparedStatement.setString(2, useridcompany);
				preparedStatement.execute();
				preparedStatement.close();
			}
		} catch (SQLException e) {
			System.out.println("Them that bai");
			e.printStackTrace();
		}
		sendResponse(resp);
	}

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		// TODO Auto-generated method stub
		jsonObject = new JSONObject();
		jsonObject.put(Status.STATUS, "SUCCESS");
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
		StringBuffer in = new StringBuffer();
		String line = null;
		JSONParser parser = new JSONParser();
		try {
			String json=null;
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null) in.append(line);
			System.out.println(in.toString());
			json=in.toString().substring(1, in.toString().length()-1);
			jsonObject= (JSONObject)parser.parse(in.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	boolean checkFollowingYet(String student, String company) {
		Connection con =  DatabaseManagement.getInstance("root","").getConnection();
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select useridstudent,useridcompany from follower"); // 
			while (rs.next()) {
				if ( rs.getString("useridstudent").equals(student) && rs.getString("useridcompany").equals(company) ) {
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
