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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;

/**
 * Servlet implementation class ReturnPostForStudent
 */
@WebServlet("/ReturnPostForStudent")
public class ReturnPostForStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReturnPostForStudent() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONArray jArrListPost = new JSONArray();
		JSONObject getJson = new JSONObject();
		
//		  String jsontest="{\"userid\":\"nguyenvana@gmail.com\"}";
//		  
//		  JSONParser parsers = new JSONParser(); try { 
//			  
//			  getJson=(JSONObject)parsers.parse(jsontest);
//			  } catch (ParseException e) { // TODO
//		  //Auto-generated catch block 
//				  e.printStackTrace(); 
//			  }
		 
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
			getJson = (JSONObject) parser.parse(json);
			System.out.println(getJson);
		} catch (Exception e) {			// crash and burn
					e.printStackTrace();
		}		
		String useridstudent = getJson.get("userid").toString();

		Connection con = DatabaseManagement.getInstance("root", "123456").getConnection();
		try {
			Statement stmtCompany = con.createStatement();
			// select * from follower f,recruitnews r, recruitmentpositions p
			// where f.useridstudent="nguyenvana@gmail.com" and
			// r.belongtocompany=f.useridcompany and p.idtag=r.ID
			// order by r.time;
			Statement stmtPosition = con.createStatement();
			ResultSet listPost = stmtCompany
					.executeQuery("select * from follower f,recruitnews r,user u where f.useridstudent=" + "\"" + useridstudent
							+ "\"" + " and r.belongtocompany=f.useridcompany and f.useridcompany= u.username order by r.time");
			while (listPost.next()) {
				JSONObject obj = new JSONObject();
				String time = listPost.getString("time");
				time = time.substring(0, time.length()- 2);
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
				
				//System.out.println(listPost.getString("avatar"));
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
