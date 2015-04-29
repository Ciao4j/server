package entity;

public class Message {
	private String label = "message";
	private String messageID = "";
	private String belongToUserAccount = "";
	private String content = "";
	private String img = "";
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	
	public String getBelongToUserAccount() {
		return belongToUserAccount;
	}
	public void setBelongToUserAccount(String belongToUserAccount) {
		this.belongToUserAccount = belongToUserAccount;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

}
