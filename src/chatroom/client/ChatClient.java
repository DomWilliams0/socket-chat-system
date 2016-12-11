package chatroom.client;

import chatroom.client.ui.ConsoleInterface;
import chatroom.client.ui.GraphicalInterface;
import chatroom.client.ui.IInterface;
import chatroom.shared.protocol.Protocol;

public class ChatClient
{
	private final String username;
	private final ClientConnection connection;
	private final IInterface ui;

	/**
	 * @param username The client's username to use in the chatroom
	 * @param ui       The user interface to use
	 */
	public ChatClient(String username, IInterface ui)
	{
		this.username = username;
		this.ui = ui;

		if (username == null ||
			username.length() < 3 ||
			username.contains(Protocol.DELIMITER) ||
			username.equals(Protocol.SERVER_USERNAME))
			throw new IllegalArgumentException("Invalid username");

		this.connection = new ClientConnection(this);
	}

	public static void main(String[] args)
	{
		if (args.length != 4)
			throw new IllegalArgumentException("Usage: <username> <gui | console> <address> <port>");

		String username = args[0];
		String uiChoice = args[1];
		String address = args[2];
		Integer port = Integer.parseInt(args[3]);

		IInterface ui;
		switch (uiChoice)
		{
			case "gui":
				ui = new GraphicalInterface();
				break;
			case "console":
				ui = new ConsoleInterface(System.out);
				break;
			default:
				throw new IllegalArgumentException("UI choice can be either \"gui\" or \"console\"");
		}

		ChatClient client = new ChatClient(username, ui);
		boolean success = client.start(address, port);

		System.exit(success ? 0 : 1);
	}

	public String getUsername()
	{
		return username;
	}

	public boolean start(String address, int port)
	{
		// connect to server
		if (!connection.connect(address, port))
		{
			return false;
		}

		// run UI
		ui.start(connection);

		// disconnect
		connection.disconnect();

		return true;
	}

	public IInterface getUI()
	{
		return ui;
	}

}
