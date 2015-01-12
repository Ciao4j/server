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

public class AddFriends extends HttpServlet {

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

		 String username1 = req.getParameter("username1");
		 String username2 = req.getParameter("username2");
		
		 String json = "";
		 if(u.addFriend(username1, username2)==1) {
			 System.out.println("success");
			 session.setAttribute("account", username1+" made friend with "+username2);
			 String login_suc = "success.jsp";
			 db.shutdown();
			 json = "{\"success\":true}";
			 resp.getWriter().write(json);
			 resp.getWriter().flush();
			 resp.getWriter().close();
			 //resp.sendRedirect(login_suc);
			 return;
		 }
		 String login_fail = "fail.jsp";
		 db.shutdown();
		 //resp.sendRedirect(login_fail);
		 json = "{\"success\":false}";
		 resp.getWriter().write(json);
		 resp.getWriter().flush();
		 resp.getWriter().close();
		 return;
	}
}