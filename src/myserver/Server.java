package myserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class Server
 */
public abstract class Server extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected JSONObject jsonObject;
    protected JSONArray jsonArray;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Server() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected abstract void sendResponse(HttpServletResponse resp);
	
    protected abstract void getParameterFromRequest(HttpServletRequest req);
}
