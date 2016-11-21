package chatroom.server;

import chatroom.ChatException;
import chatroom.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

		// spawn thread and store connection
		ClientConnection client = new ClientConnection(username, in, out, this);
		clients.put(username, client);

		Logger.log("%s connected", username);
	}

	public void removeClient(String username)
	{
		ClientConnection connection = clients.remove(username);
		if (connection != null)
		{
			Logger.log("%s disconnected", username);
			try
			{
				connection.getIn().close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				connection.getOut().close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
