package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Message;
import chatroom.shared.protocol.Protocol;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerState
{
	private static final String CHAT_HISTORY_FILE = "chat_history.ser";
	private static final int HISTORY_BUFFER_SIZE = 5;

	private final String banner;
	private final Map<String, ClientInstance> clients;
	private final List<Message> messageHistory;

	public ServerState(String banner)
	{
		if (banner == null)
		{
			banner = "";
		}

		this.banner = banner;
		this.clients = new HashMap<>();
		this.messageHistory = new ArrayList<>();

		loadChatHistory();

		Runtime.getRuntime().addShutdownHook(new Thread(this::saveChatHistory));
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
		Message m = new Message(Protocol.SERVER_USERNAME, message);
		m.encode();

		broadcastMessage(m);
	}

	public void broadcastMessage(Message message)
	{
		for (ClientInstance client : clients.values())
			ServerConnection.sendMessageToClient(client, message);

		addMessageToHistory(message);
	}

	private void addMessageToHistory(Message message)
	{
		messageHistory.add(message);

		if (messageHistory.size() % HISTORY_BUFFER_SIZE == 0)
			saveChatHistory();
	}

	public int getUserCount()
	{
		return clients.size();
	}

	public String getUserList(String delimiter)
	{
		StringBuilder sb = new StringBuilder(100);

		clients.keySet().forEach(name -> sb.append(name).append(delimiter));

		return sb.toString().trim();
	}

	public List<Message> getMessageHistory()
	{
		return messageHistory;
	}

	public void saveChatHistory()
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CHAT_HISTORY_FILE, false));
			oos.writeObject(messageHistory);
			oos.close();
			Logger.log("Saved %d messages from chat history to %s", messageHistory.size(), CHAT_HISTORY_FILE);

		} catch (IOException e)
		{
			Logger.error("Failed to save chat history: %s", e.getMessage());
		}
	}

	public void loadChatHistory()
	{
		int count = 0;
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHAT_HISTORY_FILE));

			ArrayList<Message> newHistory = (ArrayList<Message>) ois.readObject();
			messageHistory.clear();
			messageHistory.addAll(newHistory);
			count = newHistory.size();

			ois.close();
		} catch (IOException | ClassNotFoundException e)
		{
			if (!(e instanceof FileNotFoundException))
				Logger.error("Failed to load chat history: %s", e.getMessage());
		}

		Logger.log("Loaded %d messages into chat history from %s", count, CHAT_HISTORY_FILE);
	}
}
