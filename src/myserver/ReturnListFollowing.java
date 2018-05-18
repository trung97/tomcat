package myserver;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import db.DatabaseManagement;

/**
 * Servlet implementation class ReturnListFollowing
 */
@WebServlet("/returnlistfollowing")
public class ReturnListFollowing extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReturnListFollowing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONArray jArrListCompany = new JSONArray();
		JSONObject getJson = new JSONObject();
		
		StringBuilder jb = new StringBuilder();
		String line = null;
		String json = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			// report an error
			e.printStackTrace();
		}
		try {
			System.out.println("String" + jb.toString());
			json=jb.toString().substring(1, jb.toString().length()-1);
			JSONParser parser = new JSONParser();
			getJson = (JSONObject) parser.parse(json.toString());
		} catch (Exception e) {
			// crash and burn
			e.printStackTrace();
		}
		String useridstudent = getJson.get("useridstudent").toString();

		Connection con = DatabaseManagement.getInstance("root", "123456").getConnection();
		try {
			Statement stmtCompany = con.createStatement();
			ResultSet listCompany = stmtCompany
					.executeQuery("select useridcompany,fullname from follower f,user u where f.useridstudent=" + "\"" + useridstudent
							+ "\"" +" and u.username= f.useridcompany");
			while (listCompany.next()) {
				JSONObject obj = new JSONObject();
				obj.put("useridcompany", listCompany.getString("useridcompany"));
				obj.put("namecompany", listCompany.getString("fullname"));
				jArrListCompany.add(obj);
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println(jArrListCompany);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
