import java.io.Serializable;

class Message implements Serializable {
	public String nick;
	public String message;

	public Message() {
	}

	public Message(String nick, String message) {
		this.nick = nick;
		this.message = message;
	}
}
