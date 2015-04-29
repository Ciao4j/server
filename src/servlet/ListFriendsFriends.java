package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.NeoConnection;
import main.UserManagement;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class ListFriendsFriends extends HttpServlet {

	 @Override
	 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	   throws ServletException, IOException {
	  doGet(req,resp);
	 }

	 @Override
	 public void doGet(HttpServletRequest req, HttpServletResponse resp)
	   throws ServletException, IOException {
		 HttpSession session = req.getSession();
		 GraphDatabaseService db = new NeoConnection().getGraphDb();
		 UserManagement u = new UserManagement(db);
		 String json = "[";
		 
		 String username = req.getParameter("username");
		 int depth = Integer.parseInt(req.getParameter("depth"));
		 
		 System.out.println(username);
		 //List<Node> friends = u.printUserFriendByIndexIterator(username);	
		 List<Node> friends = u.printUserFriendByTraversal(username,depth);
		 
		 List<String> friendsName = new ArrayList<String>();
		 int friendsNum=0;
		 for(Node n : friends)
		 {
			 String tmp = "";
			 if(friendsNum!=0)
			 {
				 tmp += ",";
			 }
			 friendsNum++;
			 friendsName.add(n.getProperty("nickName").toString());
			 
			 List<Node> commonFriends = u.findCommonFriends(username, n.getProperty("account").toString());
			 tmp += "{\"username\":\""+n.getProperty("account").toString()+"\",\"nickname\":\""+
			 n.getProperty("nickName").toString()+"\",\"commonFriendsNum\":\""+commonFriends.size()+"\"}";
			 json += tmp;
		 }
		 json += "]";
		 System.out.println("success");
		 session.setAttribute("account", friendsName);
		 String login_fail = "showFriends.jsp";
		 db.shutdown();
		 //resp.sendRedirect(login_fail);
		 resp.getWriter().write(json);
		 resp.getWriter().flush();
		 resp.getWriter().close();
		 return;
	 	}
	}
