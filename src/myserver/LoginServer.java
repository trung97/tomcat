package myserver;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
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
import key.StudentSearchJSONKey;
import stt.Status;

@WebServlet("/LoginServer")
public class LoginServer extends Server {
	private boolean checkUser; 
	private boolean checkPass;
	private String username;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public LoginServer() {
        super();
        // TODO Auto-generated constructor stub
    }
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			System.out.println("receive reqe");
			getParameterFromRequest(req);
			
			String inputUserName= jsonObject.get("username").toString();
			String inputPassWord= jsonObject.get("password").toString();

			Connection con =  DatabaseManagement.getInstance("root","123456").getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select username,password from user"); // 
			checkUser=false;
			checkPass=false;
			while (rs.next()) {
				if ( rs.getString("username").equals(inputUserName)) {
					checkUser=true;
					username=inputUserName;
					if (rs.getString("password").equals(inputPassWord)) checkPass=true;
					break;
				}
			}
			sendResponse(resp);
			//con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	protected void sendResponse(HttpServletResponse resp) {
		//resp.setContentType("application/json");
	 //   resp.setCharacterEncoding("UTF-8");
		try {
			
			PrintWriter out =resp.getWriter();
			JSONObject obj = new JSONObject();
			if (checkUser && checkPass) {
				obj.put("STT",Status.STATUS_SUCCESS);
				Connection con =  DatabaseManagement.getInstance("root","123456").getConnection();
				Statement stmt;
				
				try {
					stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("select types from user where username=\""+username+"\"");
					while (rs.next()) {  	
						obj.put("type",rs.getString("types"));
					}
					if (obj.get("type").toString().equals("student")) {
						
						JSONObject jInfo= new JSONObject();
						Statement info = con.createStatement();
						ResultSet rsInfo = info.executeQuery("select * from user u,student s where u.username=\""+username+"\" " +"and u.username=s.userid");
						while (rsInfo.next()) {
							jInfo.put(StudentSearchJSONKey.FULL_NAME, rsInfo.getString("fullname"));
							Blob imageBlob = rsInfo.getBlob("avatar");
							if(imageBlob!=null) {
								
								//InputStream binaryStream = imageBlob.getBinaryStream(0, imageBlob.length());
								byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
								final StringBuilder builder = new StringBuilder();
							    for(byte b : bytes) {
							        builder.append(String.format("%02x", b));
							    }
							    
							    jInfo.put("avatar", builder.toString());
							}
							else {
								jInfo.put("avatar", "");
							}
						}
						
						obj.put("data", jInfo.toJSONString());
						
					}else {
						System.out.println("select * from user u,company c where u.username=\""+username+"\" " +"and u.username=c.userid");
						JSONObject jInfo= new JSONObject();
						Statement info = con.createStatement();
						ResultSet rsInfo = info.executeQuery("select * from user u,company c where u.username=\""+username+"\" " +"and u.username=c.userid");
						while (rsInfo.next()) {
							jInfo.put(StudentSearchJSONKey.FULL_NAME, rsInfo.getString("fullname"));
							Blob imageBlob = rsInfo.getBlob("avatar");
							if(imageBlob!=null) {
								
								//InputStream binaryStream = imageBlob.getBinaryStream(0, imageBlob.length());
								byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
								final StringBuilder builder = new StringBuilder();
							    for(byte b : bytes) {
							        builder.append(String.format("%02x", b));
							    }
							    
							    jInfo.put("avatar", builder.toString());
							}
							else {
								jInfo.put("avatar", "");
							}
						}
						
						obj.put("data", jInfo.toJSONString());
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
			else if (!checkUser) {
				obj.put("STT",Status.STATUS_FAIL);
				obj.put("error", "Not exist user");
				obj.put("type", "");
			}
			else {
				obj.put("STT",Status.STATUS_FAIL);
				obj.put("error", "Wrong password");
				obj.put("type", "");
			}
			System.out.println(obj.toJSONString());
			out.println(obj);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		StringBuffer in = new StringBuffer();
		String line = null;
		JSONParser parser = new JSONParser();
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null) in.append(line);
			jsonObject= (JSONObject)parser.parse(in.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		/*String json="{\"username\":\"nguyenvana@gmail.com\",\"password\":\"123\"}";
		JSONParser parser = new JSONParser();
		try {
			jsonObject= (JSONObject)parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	

	
	
	
	
}
