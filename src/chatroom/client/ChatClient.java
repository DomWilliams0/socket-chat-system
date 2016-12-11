package chatroom.client;

import chatroom.shared.protocol.Protocol;

import java.util.Scanner;

public class ChatClient
{
	private final String username;
	private final ClientConnection connection;

	/**
	 * @param username The client's username to use in the chatroom
	 */
	public ChatClient(String username)
	{
		this.username = username;

		if (username == null ||
			username.length() < 3 ||
			username.contains(Protocol.DELIMITER) ||
			username.equals(Protocol.SERVER_USERNAME))
			throw new IllegalArgumentException("Invalid username");

		this.connection = new ClientConnection(this);
	}

	public static void main(String[] args)
	{
		if (args.length != 3)
			throw new IllegalArgumentException("Usage: <username> <address> <port>");

		String username = args[0];
		String address = args[1];
		Integer port = Integer.parseInt(args[2]);

		ChatClient client = new ChatClient(username);
		boolean success = client.start(address, port);

		System.exit(success ? 0 : 1);
	}

	public String getUsername()
	{
		return username;
	}

	/**
	 * Displays the given message on the UI
	 */
	public void display(String message, Object... format)
	{
		System.out.printf(message + "\n", format);
	}

	public boolean start(String address, int port)
	{
		// connect to server
		if (!connection.connect(address, port))
		{
			return false;
		}

		// TODO UI interface
		display("Type /quit to exit");
		Scanner scanner = new Scanner(System.in);
		String line;
		while ((line = scanner.nextLine()) != null)
		{
			// quit
			if (line.equals("/quit"))
			{
				break;
			}

			// empty
			if (line.isEmpty())
			{
				continue;
			}

			// send message
			connection.sendMessage(line);
		}

		// disconnect
		connection.disconnect();

		return true;
	}

}
