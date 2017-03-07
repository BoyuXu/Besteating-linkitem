package api;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.MongoDBConnection;
import db.MySQLDBConnection;

/**
 * Servlet implementation class SearchRestaurants
 */
@WebServlet("/restaurants")
public class SearchRestaurants extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchRestaurants() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	/**protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			// 完成了对于response的改写
			 response.setContentType("application/json"); //内容type，json格式
		   	 response.addHeader("Access-Control-Allow-Origin", "*"); //不设置访问权限
		   	 String username = "";
		   	 PrintWriter out = response.getWriter(); //得到response的输出流，buffer writer
		   	 // restaurant?username-abcd
		   	 if (request.getParameter("username") != null) {
		   		 username = request.getParameter("username");
		   		 out.print("Hello " + username);
		   	 }
		   	 out.flush(); //“冲”，给用户端显示内容
		   	 out.close(); //关闭  
		}
	**/
    protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
    	/* allow access only if session exists
    			HttpSession session = request.getSession();
    			if (session.getAttribute("user") == null) {
    				response.setStatus(403);
    				return;
    			}
    	*/
    	JSONArray array = new JSONArray();
    	DBConnection connection = new MySQLDBConnection();
			if (request.getParameterMap().containsKey("user_id")
					&& request.getParameterMap().containsKey("lat")
					&& request.getParameterMap().containsKey("lon")) {
				String term = request.getParameter("term");
				String userId = request.getParameter("user_id");
				//String userId = "1111";
				double lat = Double.parseDouble(request.getParameter("lat"));
				double lon = Double.parseDouble(request.getParameter("lon"));
				// return some fake restaurants
				array = connection.searchRestaurants(userId, lat, lon, term);
			}
		RpcParser.writeOutput(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcParser.parseInput(request);
			if (input.has("user_id") && input.has("visited")) {
				String userId = (String) input.get("user_id");
				JSONArray array = (JSONArray) input.get("visited");
				List<String> visitedRestaurants = new ArrayList<>();
				for (int i = 0; i < array.length(); i++) {
					String businessId = (String) array.get(i);
					visitedRestaurants.add(businessId);
				}
				RpcParser.writeOutput(response,
						new JSONObject().put("status", "OK"));
			} else {
				RpcParser.writeOutput(response,
						new JSONObject().put("status", "InvalidParameter"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
