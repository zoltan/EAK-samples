import java.net.ServerSocket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.LinkedList;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CopyOnWriteArrayList;

class ClientHandler implements Runnable {
	private final Socket socket;
	private final List<Pair<Socket, ObjectOutputStream>> allClients;

	public ClientHandler(Socket socket, List<Pair<Socket, ObjectOutputStream>> allClients) {
		this.socket = socket;
		this.allClients = allClients;
	}

	public void publishMessage(Message message) {
		for (Pair<Socket, ObjectOutputStream> p : allClients) {
			if (p.first == this.socket)
				continue;

			try {
				p.second.writeObject(message);
				p.second.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void run() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			allClients.add(new Pair<Socket, ObjectOutputStream>(socket, oos));
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while (true) {
				Message message = (Message)ois.readObject();
				System.out.println(message.nick + "> " + message.message);
				if ("exit".equals(message.message)) {
					for(Pair<Socket, ObjectOutputStream> p : allClients) {
						if (p.first == socket) {
							p.second.close();
							allClients.remove(p);
							break;
						}
					}
					socket.close();
					return;
				}
				publishMessage(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

public class Server {
	public static void main(String[] args) {
		final ExecutorService pool =
			Executors.newFixedThreadPool(16);
		final CopyOnWriteArrayList<Pair<Socket, ObjectOutputStream>> sockets = 
		    new CopyOnWriteArrayList<Pair<Socket, ObjectOutputStream>>();
		try {
			ServerSocket serverSock = new ServerSocket(5656);
			while (true) {
				pool.execute(new ClientHandler(
						serverSock.accept(),
						sockets));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

