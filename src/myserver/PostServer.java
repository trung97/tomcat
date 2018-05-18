package myserver;

import java.io.BufferedReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import db.DatabaseManagement;
import key.PostJSONKey;
import stt.Status;
@WebServlet("/PostServer")
public class PostServer extends Server {	
	
	
	private String title;
	private String content;
	private String company;
	ArrayList<String> tags;
	ArrayList<String> positions;
	private DatabaseManagement databaseManagement;
	private String status;
	private int number;
	public PostServer() {
        super();
        // TODO Auto-generated constructor stub
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
getParameterFromRequest(req);
		
		databaseManagement = DatabaseManagement.getInstance("root", "hdl123");
		status = Status.STATUS_FAIL;
		Connection connection =  databaseManagement.getConnection();
		String query = createQuery();
		try {
			
			
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			Calendar calendar = Calendar.getInstance();
			
			java.sql.Date myDate = new java.sql.Date(calendar.getTime().getTime());
			java.text.SimpleDateFormat sdf = 
				     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentTime = sdf.format(myDate);
			preparedStatement.setString(1, currentTime);
			
			preparedStatement.setString(2, content);
			preparedStatement.setString(3, company);
			preparedStatement.setString(4, title);
			preparedStatement.execute();
			preparedStatement.close();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select max(ID) from recruitnews");
			while(rs.next()) {
				number = rs.getInt(1);
			}
			rs.close();
			statement.close();
			addTag();
			addPosition();
			
			status = Status.STATUS_SUCCESS;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sendResponse(resp);
	}

	@Override
	protected void getParameterFromRequest(HttpServletRequest req) {
		
		try {
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader br = req.getReader();
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			String jdata = sb.toString();
			JSONObject jObj = (JSONObject) (new JSONParser().parse(jdata));
			title = (String) jObj.get(PostJSONKey.TITLE);
			company = (String) jObj.get(PostJSONKey.COMPANY);
			content = (String) jObj.get(PostJSONKey.CONTENT);
			tags = new ArrayList<>();
			JSONArray jArrayTag = (JSONArray) new JSONParser().parse((String) jObj.get(PostJSONKey.TAGS));
			for(int i = 0; i< jArrayTag.size(); i++) {
				tags.add((String) jArrayTag.get(i));
			}
			
			positions = new ArrayList<>();
			JSONArray jArrayPosition = (JSONArray) new JSONParser().parse((String) jObj.get(PostJSONKey.POSITIONS));
			for(int i = 0; i< jArrayPosition.size(); i++) {
				positions.add((String) jArrayPosition.get(i));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String createQuery() {
		
		String query = " insert into recruitnews (time, content, belongtocompany, title)"
		        + " values (?, ?, ?, ?)";
		return query;
	}
	
	private void addTag() {
		Connection connection =  databaseManagement.getConnection();
		String query = " insert into tagsofcompany (content, idtag)"
		        + " values (?, ?)";
		
		try {

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			for(int i=0;i<tags.size();i++) {
				preparedStatement.setString(1, tags.get(i));
				preparedStatement.setInt(2, number);
				preparedStatement.execute();
			}
			preparedStatement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void addPosition(){
		Connection connection =  databaseManagement.getConnection();
		String query = " insert into recruitmentpositions (content, idtag)"
		        + " values (?, ?)";
		
		try {
			
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			for(int i=0;i<positions.size();i++) {
				preparedStatement.setString(1, positions.get(i));
				preparedStatement.setInt(2, number);
				preparedStatement.execute();
			}
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
