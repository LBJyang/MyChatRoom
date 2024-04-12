package HongZe.MyChatRoom.web;

public class ChatMessage {
	public String name;
	public String text;
	public long timestamp;

	public ChatMessage(String name, String text) {
		super();
		this.name = name;
		this.text = text;
		this.timestamp = System.currentTimeMillis();
	}

	public ChatMessage() {
		// TODO Auto-generated constructor stub
	}

}
