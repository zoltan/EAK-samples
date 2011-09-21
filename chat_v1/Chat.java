import java.net.ServerSocket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.LinkedList;
import java.net.Socket;

class ClientHandler implements Runnable {
	private final Socket socket;
	private final List<Socket> allClients;

	public ClientHandler(Socket socket, List<Socket> allClients) {
		this.socket = socket;
		this.allClients = allClients;
	}

	public void publishMessage(String message) {
		for (Socket socket : allClients) {
			if (socket == this.socket)
				continue;

			try {
				PrintStream out = new PrintStream(
					socket.getOutputStream());
				out.println(message);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			BufferedReader br = new BufferedReader(
						new InputStreamReader(
						socket.getInputStream()));
			while (true) {
				String line = br.readLine();
				if ("exit".equals(line)) {
					allClients.remove(socket);
					socket.close();
					return;
				}
				publishMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public class Chat {
	public static void main(String[] args) {
		final List<Socket> sockets = new LinkedList<Socket>();

		try {
			ServerSocket serverSock = new ServerSocket(5656);
			while (true) {
				Socket newClient = serverSock.accept();
				sockets.add(newClient);
				new Thread(
					new ClientHandler(newClient, sockets)
				).start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

