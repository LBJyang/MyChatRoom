package HongZe.MyChatRoom.web;

public class ChatMessage extends ChatText {
	public String name;
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

class ChatText {
	public String text;
}
