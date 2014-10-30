package data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

import com.google.gson.Gson;

import twitter4j.TwitterException;


/**
 * Servlet implementation class TwitS
 */
@WebServlet("/TwitS")

public class TwitS extends HttpServlet {
	  private static final long serialVersionUID = 1L;

	   String  count;
	  private test dao;

	  @Override
	  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Gson gson = new Gson();
			response.setContentType("application/json"); 
			String keyword=request.getParameter("keyword");
			List<message> result=new ArrayList<message>();
			System.out.println(keyword);
			try {
				result=dao.readDB(keyword);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PrintWriter out=response.getWriter();
			String ans=gson.toJson(result);
			out.println(ans);
			out.flush();
		}

	  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Gson gson = new Gson();
			response.setContentType("application/json"); 
			String keyword=request.getParameter("keyword");
			System.out.println(keyword);
			ArrayList<message> result=new ArrayList<message>();
			try {
				dao.readDB(keyword);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				result=dao.readDB(keyword);
				System.out.println(result.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i=0;i<result.size();i++)
				System.out.println(result.get(i).geoLat+" "+result.get(i).geoLong);
			System.out.println(result.size());
			PrintWriter out=response.getWriter();
			String ans=gson.toJson(result);
			out.println(ans);
			out.flush();
		}

	  
	  @Override
	  public void init() throws ServletException {
		  System.out.println("initial server");
	    
	    	 System.out.println("try to new a test class");
			try {
				dao = new test();
			} catch (TwitterException | IOException | JSONException
					| InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	  
	  public void destroy() {
	    super.destroy();
	    try {
	 
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
}
