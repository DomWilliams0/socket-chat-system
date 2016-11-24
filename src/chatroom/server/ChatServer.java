package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatServer
{
	private final String banner;
	private final Map<String, ClientInstance> clients;

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

		broadcastServerMessage(username + " joined");

		// spawn thread and store connection
		ClientInstance client = new ClientInstance(username, in, out, this);
		clients.put(username, client);

		Logger.log("%s connected", username);
	}

	public void removeClient(String username)
	{
		ClientInstance connection = clients.remove(username);
		if (connection != null)
		{
			broadcastServerMessage(username + " quit");
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

	public void broadcastServerMessage(String message)
	{
		broadcastEncodedMessage(Protocol.SERVER_USERNAME, Protocol.encodeMessage(message));
	}

	public void broadcastEncodedMessage(String username, String message)
	{
		Message m = new Message(username, message);
		for (ClientInstance client : clients.values())
		{
			ServerConnection.broadcastMessage(client, m);
		}
	}

	public static boolean runServer(int port, String banner)
	{
		ChatServer server = new ChatServer(banner);
		ServerConnection serverConnection = new ServerConnection(server);

		return serverConnection.startListening(port);
	}
}
