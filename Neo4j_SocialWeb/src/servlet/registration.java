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

import entity.User;

public class registration extends HttpServlet {
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
		 User user = new User();
		 
		 String username = req.getParameter("username");
		 String pwd = req.getParameter("pwd");
		 String nickname = req.getParameter("nickname");
		 String birthday = req.getParameter("birthday");
		 String photo = req.getParameter("photo");
		 String sex = req.getParameter("sex");

		 user.setAccount(username);
		 user.setBirthday(birthday);
		 user.setNickName(nickname);
		 user.setPassword(pwd);
		 user.setPhoto(photo);
		 user.setSex(sex);
		 String json = "";
		 System.out.println("register");
		 if(u.changeOrCreateUser(user)==1) {
				 System.out.println("success");
				 session.setAttribute("account", username);
				 String login_suc = "success.jsp";
				 db.shutdown();
				 //resp.sendRedirect(login_suc);
				 json = "{\"success\":true}";
				 resp.getWriter().write(json);
				 resp.getWriter().flush();
				 resp.getWriter().close();
				 return;
		 }
		 String login_fail = "fail.jsp";
		 db.shutdown();
		// resp.sendRedirect(login_fail);
		 json = "{\"success\":false}";
		 resp.getWriter().write(json);
		 resp.getWriter().flush();
		 resp.getWriter().close();
		 return;
	 	}

}
