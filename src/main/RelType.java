package main;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.RelationshipType;

public enum RelType implements RelationshipType {
	IS_A_FRIEND("is_a_friend"), // 好友关系
	BELONG_TO_USER("belong_to_user"); //message和发送的用户之间的关系

	private String	name;	// 定义自定义的变量

	private RelType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
