package chatroom.server;

import chatroom.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

public class ChatServer
{
	private final String banner;
	private final Map<String, ClientConnection> clients;

	public ChatServer(String banner)
	{
		if (banner == null)
		{
			banner = "";
		}

		this.banner = banner;
		this.clients = new HashMap<>();
	}

	public String getBanner()
	{
		return banner;
	}

	public void addClient(String username, BufferedReader in, BufferedWriter out) throws ChatException
	{
		// username already exists
		if (clients.containsKey(username))
		{
			throw new ChatException("There is already a client connected with the username '" + username + "'");
		}

		ClientConnection client = new ClientConnection(username, in, out);
		clients.put(username, client);
	}

	public static void main(String[] args)
	{
		ChatServer server = new ChatServer("Welcome!");
		ServerConnection serverConnection = new ServerConnection(server);
		int port = 6060;

		boolean success = serverConnection.startListening(port);
		System.exit(success ? 0 : 1);
	}
}
