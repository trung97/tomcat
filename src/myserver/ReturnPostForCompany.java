package myserver;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

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
 * Servlet implementation class ReturnPostForCompany
 */
@WebServlet("/ReturnPostForCompany")
public class ReturnPostForCompany extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReturnPostForCompany() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONArray jArrListPost = new JSONArray();
		JSONObject getJson = new JSONObject();
//		
//		  String json="{\"userid\":\"lol@gmail.com\"}";
//		  
//		 JSONParser parser = new JSONParser(); try { 
//			 getJson=(JSONObject)parser.parse(json); 
//			 } 
//		 catch (Exception e) { 
//			 // TODOAuto-generated catch block 
//			 e.printStackTrace(); 
//			 }
//		 
		StringBuffer jb = new StringBuffer();
		String line = null;
		String json= null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			// report an error
			e.printStackTrace();
		}
		try {
			json=jb.toString().substring(1, jb.toString().length()-1);
			JSONParser parser = new JSONParser();
			getJson = (JSONObject) parser.parse(json.toString());
			System.out.println(getJson);
		} catch (Exception e) {
			// crash and burn
			e.printStackTrace();
		}
		String useridcompany = getJson.get("userid").toString();

		Connection con = DatabaseManagement.getInstance("root", "123456").getConnection();
		try {
			Statement stmtCompany = con.createStatement();
			Statement stmtPosition = con.createStatement();
			ResultSet listPost = stmtCompany
					.executeQuery("select * from recruitnews r,user u where r.belongtocompany=" + "\"" + useridcompany
							+ "\"" + " and u.username="+ "\"" + useridcompany
							+ "\""+ "order by r.time");
			while (listPost.next()) {
				JSONObject obj = new JSONObject();
				String time = listPost.getString("time");
				time = time.substring(0, time.length() -2);
				obj.put("time", time);
				obj.put("namecompany", listPost.getString("fullname"));
				obj.put("title", listPost.getString("title"));
				obj.put("content", listPost.getString("content"));
				
				Blob imageBlob = listPost.getBlob("avatar");
				if(imageBlob!=null) {
					
					//InputStream binaryStream = imageBlob.getBinaryStream(0, imageBlob.length());
					byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
					final StringBuilder builder = new StringBuilder();
				    for(byte b : bytes) {
				        builder.append(String.format("%02x", b));
				    }
				    
					obj.put("avatar", builder.toString());
				}
				else {
					obj.put("avatar", "");
				}
				JSONArray arrPosi = new JSONArray();
				ResultSet listPosition = stmtPosition
						.executeQuery("select * from recruitmentpositions where idtag=" + listPost.getString("ID"));
				while (listPosition.next()) {
					JSONObject pos = new JSONObject();
					pos.put("position", listPosition.getString("content"));
					arrPosi.add(pos);
				}
				obj.put("listPosition", arrPosi);
				jArrListPost.add(obj);
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println(jArrListPost);
			System.out.println(jArrListPost);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
