package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.neo4j.graphdb.GraphDatabaseService;

import main.NeoConnection;
import main.UserManagement;


public class CheckAccount extends HttpServlet {

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
//	 AccountBean account = new AccountBean();
	 String username = req.getParameter("username");
	 String pwd = req.getParameter("pwd");
//	 account.setPassword(pwd);
//	 account.setUsername(username);
	 
	 
//	 if((username != null)&&(username.trim().equals("jsp"))) {
//		 if((pwd != null)&&(pwd.trim().equals("1"))) {
//			 System.out.println("success");
////    session.setAttribute("account", account);
//			 session.setAttribute("account", username);
//			 String login_suc = "success.jsp";
//			 resp.sendRedirect(login_suc);
//			 return;
//		 }
//	 }
	 
	 if(u.login(username, pwd)==1) {
			 System.out.println("success");
			 session.setAttribute("account", username);
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