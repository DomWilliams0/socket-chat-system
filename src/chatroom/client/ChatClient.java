package chatroom.client;

import chatroom.Connection;

public class ChatClient
{
	private final String username;
	private Connection connection;

	/**
	 * @param username The client's username to use in the chatroom
	 */
	public ChatClient(String username)
	{
		this.username = username;
		this.connection = new Connection(username);

		// TODO verify username is not null, is minimum length, has no new lines in it etc.
	}

	/**
	 * Connect to a chat server
	 *
	 * @param address The server's address
	 * @param port    The server's port
	 * @return If the connection was a success
	 */
	public boolean connect(String address, int port)
	{
		return connection.connect(address, port);
	}

	public void disconnect()
	{
		// TODO
	}

	/**
	 * @return The client's username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return If the client is connected to a server
	 */
	public boolean isConnected()
	{
		return connection.isConnected();
	}


	public static void main(String[] args)
	{
		ChatClient client = new ChatClient("user");
		boolean success = client.connect("localhost", 6060);
		System.out.println("success = " + success);
	}
}
