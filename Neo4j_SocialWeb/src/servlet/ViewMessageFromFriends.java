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

public class ViewMessageFromFriends extends HttpServlet {

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
		 
		 List<Node> messages = u.viewMessageFromFriends(username, u);		
		 List<String> content = new ArrayList<String>();
		 for(Node n : messages)
		 {
			 content.add(n.getProperty("content").toString());
		 }

		 System.out.println("success");
		 session.setAttribute("account", content);
		 String login_fail = "showFriends.jsp";
		 db.shutdown();
		 resp.sendRedirect(login_fail);
		 return;
	 	}
	}