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

public class getUserInfo extends HttpServlet {
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

		 String username = req.getParameter("username");
		 String searchUsername = req.getParameter("searchUsername");
		 Node n = u.getUserByAccount(searchUsername);
		 
		 List<Node> myFriends = u.printUserFriendByIndexIterator(username);
		 List<String> fName = new  ArrayList<String>();
		 for(Node m:myFriends)
		 {
			 fName.add(m.getProperty("account").toString());
		 }
		 Boolean isFriend = false;
		 if(fName.contains(n.getProperty("account")))
		 {
			 isFriend = true;
		 }
		 
		 String json = "";
		
		 json += "{\"username\":\""+n.getProperty("account").toString()+"\",\"nickname\":\""+
				 n.getProperty("nickName").toString()+"\",\"gender\":\""+n.getProperty("sex").toString()
				 +"\",\"birthday\":\""+n.getProperty("birthday").toString()+"\",\"isFriend\":\""+isFriend+"\"}";


		 System.out.println("success");

		 String login_fail = "showFriends.jsp";
		 db.shutdown();
		 //resp.sendRedirect(login_fail);
		 resp.getWriter().write(json);
		 resp.getWriter().flush();
		 resp.getWriter().close();
		 return;
	 	}

}
