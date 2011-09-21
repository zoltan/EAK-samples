import java.net.ServerSocket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.InetAddress;

class ServerHandler implements Runnable {
	private final Socket socket;

	public ServerHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(
								socket.getInputStream());
			while (true) {
				Message message = (Message)ois.readObject();
				System.out.println(message.nick + "> " + message.message);
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
	}
}

public class Client {
	private static void sendMessage(ObjectOutputStream oos,
					String nick,
					String message) throws IOException {
		System.out.println(nick + "> " + message);
		oos.writeObject(new Message(nick, message));
		oos.flush();
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		int port = 5656;

		Socket socket = new Socket(addr, port);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		new Thread(new ServerHandler(socket)).start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String line = br.readLine();
			if ("exit".equals(line)) {
				sendMessage(oos, args[0], line);
				oos.close();
				return;
			}
			sendMessage(oos, args[0], line);
		}
	}
}

