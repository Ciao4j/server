import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;










import entity.Message;
import entity.User;



public class UserManagement {
	private GraphDatabaseService graphDb;
	private static Index<Node> nodeIndex;
	private static Index<Relationship>	relationshipIndex;
	
	public UserManagement(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
//		emailList = new MessageDao().getMessageByIndex(1,10001);
//		sessionList = new SessionDao().getSessionByIndex(1,5001);
	}
	public UserManagement() {
		this.graphDb = new NeoConnection().getGraphDb();
//		emailList = new MessageDao().getMessageByIndex(1,10001);
//		sessionList = new SessionDao().getSessionByIndex(1,5001);
	}
	
	public void createUser(User user) {
		
	}
	
	//修改用户资料，若用户不存在，则创建用户；用户节点主键为account
	public void changeOrCreateUser(User user) {
		Transaction tx = graphDb.beginTx();
		UniqueFactory<Node> factory;
		try {
			factory = new UniqueFactory.UniqueNodeFactory(graphDb,"user") {
				
				@Override
				protected void initialize(Node created, Map<String, Object> properties) {
					created.setProperty("account", properties.get("account"));
				}
			};
		}
		finally {
			tx.finish();
		}		
		tx = graphDb.beginTx();
		try {	
			nodeIndex = graphDb.index().forNodes("user");
			Node node = (Node) factory.getOrCreate("account", user.getAccount());
			node.setProperty("label", user.getLabel());
			node.setProperty("nickName", user.getNickName());
			node.setProperty("birthday", user.getBirthday());
			node.setProperty("password", user.getPassword());
			node.setProperty("sex", user.getSex());

			nodeIndex.add(node, "account", user.getAccount());
			nodeIndex.add(node, "label", user.getLabel());
			tx.success();
		}finally {
			tx.finish();
		}	
	}

	//添加好友
	public void addFriend(String fromAccount,String toAccount){
		relationshipIndex = graphDb.index().forRelationships("relationshipIndex");
		Index<Node> Index = graphDb.index().forNodes("user");
		Node fromNode = Index.get("account", fromAccount).getSingle();
		Node toNode = Index.get("account", toAccount).getSingle();
		System.out.println(fromNode+"  "+toNode);
		if(fromNode==null||toNode==null)
		{
			return ;
		}
		Transaction tx = graphDb.beginTx();
		try{
			Relationship rel = fromNode.createRelationshipTo(toNode,RelType.IS_A_FRIEND);
			rel.setProperty("name", RelType.IS_A_FRIEND.getName());
			relationshipIndex.add(rel,"name",RelType.IS_A_FRIEND.getName());
			
			rel = toNode.createRelationshipTo(fromNode,RelType.IS_A_FRIEND);
			rel.setProperty("name", RelType.IS_A_FRIEND.getName());
			relationshipIndex.add(rel,"name",RelType.IS_A_FRIEND.getName());
			tx.success();
		}finally {
			tx.finish();
		}
	}
	
	//发布消息，创建message节点（主键为messageID），并创建message节点和user节点间的关系
	public void publishMessage(String userAccount,String messageContent)
	{
		Message message = new Message();
		message.setLabel("message");
		message.setBelongToUserAccount(userAccount);
		message.setContent(messageContent);
		message.setMessageID(UUID.randomUUID().toString());
		
		Transaction tx = graphDb.beginTx();
		UniqueFactory<Node> factory;
		try {
			factory = new UniqueFactory.UniqueNodeFactory(graphDb,"message") {
				
				@Override
				protected void initialize(Node created, Map<String, Object> properties) {
					created.setProperty("messageID", properties.get("messageID"));
				}
			};
		}
		finally {
			tx.finish();
		}		
		tx = graphDb.beginTx();
		try {	
			nodeIndex = graphDb.index().forNodes("message");
			Node node = (Node) factory.getOrCreate("messageID", message.getMessageID());
			createRelationshipFromMessageToUser(node,message.getBelongToUserAccount());
			node.setProperty("label", message.getLabel());
			node.setProperty("belongtouser", message.getBelongToUserAccount());
			node.setProperty("content", message.getContent());

			nodeIndex.add(node, "messageID", message.getMessageID());
			nodeIndex.add(node, "label", message.getLabel());
			tx.success();
		}finally {
			tx.finish();
		}		
	}
	//创建message到对应user节点间的关系
	public void createRelationshipFromMessageToUser(Node node,String userAccount)
	{
		relationshipIndex = graphDb.index().forRelationships("relationshipIndex");
		Index<Node> Index = graphDb.index().forNodes("user");
		Node userNode = Index.get("account", userAccount).getSingle();
		if(userNode==null || node==null )
		{
			return ;
		}
		Transaction tx = graphDb.beginTx();
		try{
			Relationship rel = node.createRelationshipTo(userNode,RelType.BELONG_TO_USER);
			rel.setProperty("name", RelType.BELONG_TO_USER.getName());
			relationshipIndex.add(rel,"name",RelType.BELONG_TO_USER.getName());
			tx.success();
		}finally {
			tx.finish();
		}		
	}
	
	//通过遍历关系索引来找出和acount直接关联的朋友
	public void printUserFriendByIndexIterator(String acount)
	{
		Index<Node> Index = graphDb.index().forNodes("user");
		relationshipIndex = graphDb.index().forRelationships("relationshipIndex");
		
		Node fromNode = Index.get("account", acount).getSingle();
		Iterator rel = relationshipIndex.query("name", RelType.IS_A_FRIEND.getName()).iterator();
		while(rel.hasNext()) {
			Relationship N = (Relationship) rel.next() ;
			String name = (String) N.getStartNode().getProperty("account");
			if ( !name.equals(acount) )
				continue;
			System.out.println((String) N.getStartNode().getProperty("nickName")+
					" has a friend: "+(String) N.getEndNode().getProperty("nickName"));
		}
		
	}
	
	//递归宽搜找出acount的所有朋友及朋友的朋友
	public void printUserFriendByTraversal(String acount)
	{
		Index<Node> Index = graphDb.index().forNodes("user");
		relationshipIndex = graphDb.index().forRelationships("relationshipIndex");
		
		Node fromNode = Index.get("account", acount).getSingle();
		
		TraversalDescription td = Traversal.description()
		        // 这里是广度优先，也可以定义为深度优先遍历
		        .breadthFirst()
		        // 这里定义边类型必须为KNOWS，且必须都为出边
		        .relationships(RelType.IS_A_FRIEND, Direction.OUTGOING)
		        // 排除开始顶点
		        .evaluator(Evaluators.excludeStartPosition());
		int friendsNumbers = 0;
		for(Path friendPath : td.traverse(fromNode)){
			System.out.println("At depth " + friendPath.length() + " => "
		            + friendPath.endNode().getProperty("nickName"));
		        friendsNumbers++;
		}
		
		System.out.println("Number of friends found: " + friendsNumbers);

		
	}
	
	public static void main(String args[]){
		GraphDatabaseService db = new NeoConnection().getGraphDb();
		User user = new User();
		user.setAccount("33");
		user.setBirthday("19913344");
		user.setNickName("Cahill");
		user.setPassword("33");
		user.setSex("female");
		user.setUserID("3");
		UserManagement it = new UserManagement(db);
		System.out.println("start building");
//		it.changeOrCreateUser(user);
		//it.addFriend("22", "33");
		//it.printUserFriendByIndexIterator("11");
		it.printUserFriendByTraversal("11");
		db.shutdown();
		System.out.println("end building");
	}
	
}
