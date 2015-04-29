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

public class ViewMessageFromFriend extends HttpServlet {

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
		 String json = "[";
		 List<Node> messages = u.viewMessageFromFriend(username);		
		 List<String> content = new ArrayList<String>();
		 int messageNum=0;
		 for(Node n : messages)
		 {
			 String tmp = "";
			 if(messageNum!=0)
			 {
				 tmp += ",";
			 }
			 messageNum++;
			 content.add(n.getProperty("content").toString());
			 
			 tmp += "{\"username\":\""+n.getProperty("belongtouser").toString()+"\",\"nickname\":\""+
					 u.getNickNameByAccount(n.getProperty("belongtouser").toString())+"\",\"date\":\""+n.getProperty("date").toString()
					 +"\",\"content\":\""+n.getProperty("content").toString()+"\"}";
			 json += tmp;
		 }
		 json += "]";
		 System.out.println(json);
		 System.out.println("success");
		 session.setAttribute("account", content);
		 String login_fail = "showFriends.jsp";
		 db.shutdown();
		 //resp.sendRedirect(login_fail);
		 resp.getWriter().write(json);
		 resp.getWriter().flush();
		 resp.getWriter().close();
		 return;
	 	}
	}