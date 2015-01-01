package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.NeoConnection;
import main.UserManagement;

import org.neo4j.graphdb.GraphDatabaseService;

public class PublishMessage extends HttpServlet {

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
		 String message = req.getParameter("message");
		
		 if(u.publishMessage(username, message)==1) {
			 System.out.println("success");
			 session.setAttribute("account", username+" published "+message);
			 String login_suc = "success.jsp";
			 db.shutdown();
			 resp.sendRedirect(login_suc);
			 return;
		 }
		 String login_fail = "fail.jsp";
		 db.shutdown();
		 resp.sendRedirect(login_fail);
		 return;
	}
}