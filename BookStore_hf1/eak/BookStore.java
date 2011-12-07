import java.util.*;
import java.net.*;
import java.util.Map.Entry;
import java.io.*;
import java.sql.*;
import java.util.concurrent.*;

class Message {
    private final String verb;
    private final Map<String, String> parameters;
    
    public Message(String verb) {
	this.verb = verb;
	parameters = new HashMap<String, String>();
    }
    
    public String getVerb() {
	return verb;
    }
    
    public void addParameter(String key, String value) {
	parameters.put(key, value);
    }
    
    public Map<String, String> getParameters() {
	return new HashMap<String, String>(parameters);
    }
    
    public String getParameter(String key) {
	return parameters.get(key);
    }
    
    public boolean hasParameters(String... keys) {
	for(String k : keys)
	    if(parameters.get(k) == null)
		return false;
	return true;
    }
}

class MessageHandlerFactory {
    static class DefaultErrorMessageHandler implements MessageHandler {
	public Message processIncomingMessage(Message incomingMessage) {
	    return new Message("ERROR");
	}
    }

    static private final ConcurrentHashMap<String, MessageHandler> handlers =
	new ConcurrentHashMap<String, MessageHandler>();
    static private final MessageHandler defaultErrorMessageHandler =
	new DefaultErrorMessageHandler();

    public static void registerMessageHandler(String verb, MessageHandler handler) {
	handlers.put(verb, handler);
    }

    public static MessageHandler getMessageHandler(Message message) {
	final MessageHandler messageHandler = handlers.get(message.getVerb());
	if(messageHandler == null)
	    return defaultErrorMessageHandler;
	return messageHandler;
    }
    
    public static MessageHandler getErrorMessageHandler() {
	return defaultErrorMessageHandler;
    }
}

interface MessageHandler {
    public Message processIncomingMessage(Message incomingMessage);
}

class AddPieceMessageHandler implements MessageHandler {
    public Message processIncomingMessage(Message incomingMessage) {
	if(!incomingMessage.hasParameters("Id", "Pieces"))
	    return null;
	if(Integer.parseInt(incomingMessage.getParameter("Pieces")) < 0)
	    return null;
	Message reply = new Message("OK");
	try {
	    boolean rossz = false;

	    PreparedStatement st = BookStore.getConnection().prepareStatement("SELECT PIECES FROM NZABOOK WHERE ID=?");
	    st.setInt(1, Integer.parseInt(incomingMessage.getParameter("Id")));
	    ResultSet rs = st.executeQuery();
	    if(!rs.next())
		rossz = true;
	    rs.close();
	    st.close();
	    
	    if(rossz)
		return null;
	    
	    st = BookStore.getConnection().prepareStatement("UPDATE NZABOOK SET PIECES=PIECES+? WHERE ID=?");
	    st.setInt(1, Integer.parseInt(incomingMessage.getParameter("Pieces")));
	    st.setInt(2, Integer.parseInt(incomingMessage.getParameter("Id")));
	    st.executeUpdate();
	    st.close();
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return reply;
    }
}

class RemovePieceMessageHandler implements MessageHandler {
    public Message processIncomingMessage(Message incomingMessage) {
	System.out.println("juj!");
	System.out.println("-" + incomingMessage.getParameter("Pieces"));
	if(!incomingMessage.hasParameters("Id", "Pieces"))
	    return null;
	try {
	    Integer.parseInt(incomingMessage.getParameter("Id"));
	} catch(NumberFormatException e) {
	    return null;
	}
	if(Integer.parseInt(incomingMessage.getParameter("Pieces")) < 0)
	    return null;
	Message reply = new Message("OK");
	try {
	    boolean rossz = false;
	    
	    PreparedStatement stx = BookStore.getConnection().prepareStatement("SELECT PIECES FROM NZABOOK WHERE ID=?");
	    stx.setInt(1, Integer.parseInt(incomingMessage.getParameter("Id")));
	    ResultSet rs = stx.executeQuery();
	    int pieces = 0;
	    if(!rs.next())
		rossz = false;
	    else
	        pieces = rs.getInt(1);
	    rs.close();
	    stx.close();
	    
	    if(rossz)
		return null;
	    if(pieces-Integer.parseInt(incomingMessage.getParameter("Pieces")) < 0)
		return null;
	    
	    PreparedStatement st = BookStore.getConnection().prepareStatement("UPDATE NZABOOK SET PIECES=PIECES-? WHERE ID=?");
	    st.setInt(1, Integer.parseInt(incomingMessage.getParameter("Pieces")));
	    st.setInt(2, Integer.parseInt(incomingMessage.getParameter("Id")));
	    st.executeUpdate();
	    st.close();
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return reply;
    }
}


class AddAuthorMessageHandler implements MessageHandler {
    public Message processIncomingMessage(Message incomingMessage) {
	if(!incomingMessage.hasParameters("Name"))
	    return null;
	if(incomingMessage.getParameter("Name").length() == 0)
	    return null;
	Message reply = new Message("OK");
	try {
	    PreparedStatement st = BookStore.getConnection().prepareStatement("INSERT INTO NZAAUTHOR(NAME) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
	    st.setString(1, incomingMessage.getParameter("Name"));
	    st.executeUpdate();
	    ResultSet result = st.getGeneratedKeys();
	    result.next();
	    reply.addParameter("Id", Integer.toString(result.getInt(1)));
	    result.close();
	    st.close();
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return reply;
    }
}

class AddBookMessageHandler implements MessageHandler {
    public Message processIncomingMessage(Message incomingMessage) {
	if(!incomingMessage.hasParameters("Authors", "Title", "Pieces"))
	    return null;
	try {
	    if(Integer.parseInt(incomingMessage.getParameter("Pieces")) < 0)
		return null;
	} catch(NumberFormatException e) {
	    return null;
	}
	if(incomingMessage.getParameter("Authors").length() == 0)
	    return null;
	if(incomingMessage.getParameter("Title").length() == 0)
	    return null;

	for(String s : incomingMessage.getParameter("Authors").split(",")) {
	    try {
		Integer.parseInt(s);
	    } catch(NumberFormatException e) {
		return null;
	    }
	}

	Message reply = new Message("OK");
	try {
	    for(String s : incomingMessage.getParameter("Authors").split(",")) {
		PreparedStatement sta = BookStore.getConnection().prepareStatement("SELECT COUNT(*) FROM NZAAUTHOR WHERE ID = ?");
		sta.setInt(1, Integer.parseInt(s));
		ResultSet result = sta.executeQuery();
		result.next();
		int k = result.getInt(1);
		result.close();
		sta.close();
		if(k != 1)
		    return null;
	    }

	    Integer bookId = -1;
	    PreparedStatement st = BookStore.getConnection().prepareStatement("INSERT INTO NZABOOK(TITLE, PIECES) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
	    st.setString(1, incomingMessage.getParameter("Title"));
	    st.setInt(2, Integer.parseInt(incomingMessage.getParameter("Pieces")));
	    st.executeUpdate();
	    ResultSet result = st.getGeneratedKeys();
	    result.next();
	    bookId = result.getInt(1);
	    reply.addParameter("Id", bookId.toString());
	    result.close();
	    st.close();
	    
	    for(String s : incomingMessage.getParameter("Authors").split(",")) {
		PreparedStatement stb = BookStore.getConnection().prepareStatement("INSERT INTO NZABOOK_NZAAUTHOR VALUES(?, ?)");
		stb.setInt(1, Integer.parseInt(s));
		stb.setInt(2, bookId);
		stb.executeUpdate();
		stb.close();
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return reply;
    }
}


class ClientHandler extends Thread {
    private final Socket socket;

    public ClientHandler(Socket socket) {
	this.socket = socket;
    }
    
    private static Message parseMessage(InputStream inputStream) {
	if(inputStream == null)
	    return null;

	Message message = null;
	String line;
	final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

	try {
	    boolean first = true;
	    while((line = in.readLine()) != null) {
		if(line.equals(""))
		    break;
		if(!first) { // this must be a paramter type, in the form of 'ParameterName: value'
		    int separatorPos = line.indexOf(':');
		    if(separatorPos == -1 || separatorPos + 1 > line.length() - 1 || (line.charAt(separatorPos + 1) != ' '))
			return null;
		    String key = line.substring(0, separatorPos);
		    String value = line.substring(separatorPos + 2);
		    if(value.length() <= 0)
			return null;
		    message.addParameter(key, value);
		} else { // the first line must be a valid verb
		    try {
			message = new Message(line);
		    } catch(IllegalArgumentException iae) {
			return null;
		    }
		    first = false;
		}
	    }
	} catch(IOException e) {
	    return null;
	}
	
	return message;
    }
    
    public static void sendMessage(OutputStream outputStream, Message message) {
	final PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream));
	
	out.println(message.getVerb());
	for(Entry<String, String> entry : message.getParameters().entrySet())
	    out.println(entry.getKey() + ": " + entry.getValue());
	out.flush();
    }
    
    public void run() {
	MessageHandler messageHandler;

	try {
	    Message incomingMessage = parseMessage(socket.getInputStream());	
	    if(incomingMessage == null)
		messageHandler = MessageHandlerFactory.getErrorMessageHandler();
	    else
		messageHandler = MessageHandlerFactory.getMessageHandler(incomingMessage);

	    Message replyMessage = messageHandler.processIncomingMessage(incomingMessage);
	    if(replyMessage == null)
		replyMessage = MessageHandlerFactory.getErrorMessageHandler().processIncomingMessage(incomingMessage);
	    sendMessage(socket.getOutputStream(), replyMessage);
	} catch(IOException e) {
	    e.printStackTrace();
	}

	try {
	    socket.close();
	} catch(IOException ex) {
	    ex.printStackTrace();
	}
    }
}

public class BookStore {
    static final int DEFAULT_PORT = 1239;
    static Connection connection;

    public static Connection getConnection() {
	return connection;
    }

    public static void main(String[] args) {
	ServerSocket sock = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://twilight.softinvest.net/javagyak",
                                    "javagyak", "almafa");
            Statement st = connection.createStatement();
    	    st.executeUpdate("DELETE FROM NZABOOK_NZAAUTHOR");
    	    st.executeUpdate("DELETE FROM NZABOOK");
    	    st.executeUpdate("DELETE FROM NZAAUTHOR");
            st.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

	try {
	    sock = new ServerSocket(DEFAULT_PORT);
	} catch(IOException e) {
	    e.printStackTrace();
	}
	
	MessageHandlerFactory.registerMessageHandler("ADDAUTHOR", new AddAuthorMessageHandler());
	MessageHandlerFactory.registerMessageHandler("ADDBOOK", new AddBookMessageHandler());
	MessageHandlerFactory.registerMessageHandler("ADDPIECE", new AddPieceMessageHandler());
	MessageHandlerFactory.registerMessageHandler("REMOVEPIECE", new RemovePieceMessageHandler());

	while(true) {
	    Socket socket = null;
	    try {    
	        socket = sock.accept();
	    } catch(Exception e) {
	    }
	    
	    ClientHandler ch = new ClientHandler(socket);
	    ch.start();
	}
    }
}
