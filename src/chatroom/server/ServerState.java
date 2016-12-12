package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Message;
import chatroom.shared.protocol.Command;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a server's state
 */
public class ServerState
{
	private static final String CHAT_HISTORY_FILE = "chat_history.ser";
	private static final int HISTORY_BUFFER_SIZE = 5;

	private final String banner;
	private final Map<String, ClientInstance> clients;
	private final List<Message> messageHistory;

	/**
	 * Previous chat history will be loaded from CHAT_HISTORY_FILE, if it exists
	 *
	 * @param banner An optional message to send to clients on connecting
	 */
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

	/**
	 * @return The server banner
	 */
	public String getBanner()
	{
		return banner;
	}

	/**
	 * Adds the given client to the server
	 *
	 * @param username The client's username
	 * @param in       The client's input stream reader
	 * @param out      The client's output stream writer
	 * @throws ChatException If the client should be rejected
	 */
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

	/**
	 * Removes the given client from the server
	 *
	 * @param username The client's username
	 */
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

	/**
	 * Broadcasts the given string to all clients from the reserved server "user"
	 *
	 * @param message The string to broadcast
	 */
	public void broadcastServerMessage(String message)
	{
		Message m = new Message(Command.SERVER_USERNAME, message);
		m.encode();

		broadcastMessage(m);
	}

	/**
	 * Broadcasts the given message to all clients
	 *
	 * @param message The message to broadcast
	 */
	public void broadcastMessage(Message message)
	{
		for (ClientInstance client : clients.values())
			ServerConnection.sendMessageToClient(client, message);

		addMessageToHistory(message);
	}

	/**
	 * Adds the given message to the chat history
	 * History will be saved to file once the buffer is filled
	 *
	 * @param message The message to save
	 */
	private void addMessageToHistory(Message message)
	{
		messageHistory.add(message);

		if (messageHistory.size() % HISTORY_BUFFER_SIZE == 0)
			saveChatHistory();
	}

	/**
	 * @return The number of users connected to the server
	 */
	public int getUserCount()
	{
		return clients.size();
	}

	/**
	 * @param delimiter The delimiter for the returned list of usernames
	 * @return The list of clients connected to the server, separated by the given delimiter
	 */
	public String getUserList(String delimiter)
	{
		StringBuilder sb = new StringBuilder(100);

		clients.keySet().forEach(name -> sb.append(name).append(delimiter));

		return sb.toString().trim();
	}

	/**
	 * @return The full message history
	 */
	public List<Message> getMessageHistory()
	{
		return messageHistory;
	}

	/**
	 * Saves the chat history buffer to file
	 */
	private void saveChatHistory()
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

	/**
	 * Loads the chat history from file
	 */
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
