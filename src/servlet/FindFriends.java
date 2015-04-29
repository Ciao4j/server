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

public class FindFriends extends HttpServlet {

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
		 List<Node> myFriends = u.printUserFriendByIndexIterator(username);
		 List<String> fName = new  ArrayList<String>();
		 for(Node n:myFriends)
		 {
			 fName.add(n.getProperty("account").toString());
		 }
		 String searchUserName = req.getParameter("searchUsername");
		 String birth = req.getParameter("birthday");
		 String nickname = req.getParameter("nickname");
		 String sex = req.getParameter("sex");
		 List<Node> friends = u.searchUserByAll(searchUserName,birth, nickname, sex);
		 String json = "[";
		 List<String> friendsName = new ArrayList<String>();
		 int friendsNum=0;
		 for(Node n : friends)
		 {
			 if(n.getProperty("account").toString().equals(username.toString()))
			 {
				 continue;
			 }
			 System.out.println(n.getProperty("account").toString()+n.getProperty("nickName").toString()+username.toString());
			 String tmp = "";
			 if(friendsNum!=0)
			 {
				 tmp += ",";
			 }
			 friendsNum++;
			 friendsName.add(n.getProperty("nickName").toString());
			 Boolean isFriend = false;
			 if(fName.contains(n.getProperty("account")))
			 {
				 isFriend = true;
			 }
			 List<Node> commonFriends = u.findCommonFriends(username, n.getProperty("account").toString());
			 tmp += "{\"username\":\""+n.getProperty("account").toString()+"\",\"nickname\":\""+
			 n.getProperty("nickName").toString()+"\",\"commonFriendsNum\":\""+commonFriends.size()+"\",\"isFriend\":"
			 +isFriend+"}";
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
