package myserver;

import java.awt.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;

/**
 * Servlet implementation class SearchCompany
 */
@WebServlet("/SearchCompany")
public class SearchPostOfCompany extends Server {
	private static final long serialVersionUID = 1L;
	JSONArray jArrListPost = new JSONArray();

	 
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			getParameterFromRequest(req);

			String useridstudent= jsonObject.get("userid").toString();
			//int tag= Integer.parseInt(jsonObject.get("tag").toString());

			Connection con =  DatabaseManagement.getInstance("root","123456").getConnection();
			Statement stmtCompany = con.createStatement();
			ResultSet listCompany = stmtCompany.executeQuery("select useridcompany from follower where useridstudent = \""+useridstudent+"\"" );
			//ResultSet rs1 = stmt.executeQuery("select time,	 from post"); // 
			//System.out.println(listCompany.getString("useridcompany"));
			JSONArray jArray = new JSONArray();
			while (listCompany.next()) {
				JSONObject obj= new JSONObject();
				obj.put("useridcompany", listCompany.getString("useridcompany"));
				jArray.add(obj);
			}
			//listCompany.close();
			Statement stmtpost = con.createStatement();
			Statement stmtposition = con.createStatement();
			for(int i=0;i<jArray.size();i++) {
				JSONObject company=(JSONObject) jArray.get(i);
				ResultSet listpost= stmtpost.executeQuery("select * from recruitnews where belongtocompany =\""+company.get("useridcompany")+"\"");
				JSONArray listPostofaCompany = new JSONArray();
				while (listpost.next()) {
					JSONObject post = new JSONObject();
					post.put("time", listpost.getString("time"));
					post.put("content",listpost.getString("content"));
					JSONArray jArrPosition = new JSONArray();
					ResultSet listposition= stmtposition.
							executeQuery("select content from recruitmentpositions where idtag =\""+listpost.getString("ID")+"\"");
					while(listposition.next()) {
						JSONObject position = new JSONObject();
						position.put("position",listposition.getString("content"));
						jArrPosition.add(position);
						}
					post.put("listposition", jArrPosition);
					listPostofaCompany.add(post);
				}
				//listpost.close();
				JSONObject companyPost = new JSONObject();
				companyPost.put("company", company.get("useridcompany"));
				companyPost.put("listpost", listPostofaCompany);
				jArrListPost.add(companyPost);
			}
			
			sendResponse(resp);
			//con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		// TODO Auto-generated method stub
		try {
			PrintWriter out =resp.getWriter();
			out.println(jArrListPost);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		// TODO Auto-generated method stub
/*		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			 report an error  }

		try {
			JSONParser parser = new JSONParser();
			jsonObject = (JSONObject) parser.parse(jb.toString());
		} catch (Exception e) {
			// crash and burn
			e.printStackTrace();
		}*/
		String json="{\"userid\":\"nguyenvana@gmail.com\"}";
		
		JSONParser parser = new JSONParser();
		try {
			jsonObject= (JSONObject)parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
