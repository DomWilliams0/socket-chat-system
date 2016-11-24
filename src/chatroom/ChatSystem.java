package chatroom;

import chatroom.client.ChatClient;
import chatroom.server.ChatServer;

import java.util.Arrays;

public class ChatSystem
{
	private enum ChatType
	{
		SERVER, CLIENT
	}

	private ChatType type;
	private int port;
	private String address;

	private String username;

	private String serverBanner;

	private void parseArgs(String[] args) throws IllegalArgumentException
	{
		// no type given
		if (args.length < 1)
			throw new IllegalArgumentException("Usage: <server | client> ...");

		// invalid type
		String typeString = args[0].toLowerCase();
		switch (typeString)
		{
			case "server":
				parseServerArgs(args);
				break;
			case "client":
				parseClientArgs(args);
				break;
			default:
				throw new IllegalArgumentException("Invalid chat type");
		}
	}

	private void parseClientArgs(String[] args) throws IllegalArgumentException
	{
		if (args.length != 4)
			throw new IllegalArgumentException("Usage: client <username> <address> <port>");

		type = ChatType.CLIENT;
		username = args[1];
		address = args[2];
		port = Integer.parseInt(args[3]);
	}

	private void parseServerArgs(String[] args) throws IllegalArgumentException
	{
		if (args.length < 4)
			throw new IllegalArgumentException("Usage: server <address> <port> <banner>");

		type = ChatType.SERVER;
		address = args[1];
		port = Integer.parseInt(args[2]);

		String[] bannerArgs = Arrays.copyOfRange(args, 3, args.length);
		serverBanner = String.join(" ", bannerArgs);
	}

	public ChatType getType()
	{
		return type;
	}

	public int getPort()
	{
		return port;
	}

	public String getAddress()
	{
		return address;
	}

	public String getUsername()
	{
		return username;
	}

	public String getServerBanner()
	{
		return serverBanner;
	}

	public static void main(String[] args)
	{
		ChatSystem system = new ChatSystem();

		try
		{
			system.parseArgs(args);
		} catch (RuntimeException e)
		{
			Logger.error(e.getMessage());
			System.exit(1);
			return;
		}

		boolean success = false;
		switch (system.getType())
		{
			case SERVER:
				success = ChatServer.runServer(system.getPort(), system.getServerBanner());
				break;
			case CLIENT:
				success = ChatClient.runClient(system.getAddress(), system.getPort(), system.getUsername());
				break;
		}

		System.exit(success ? 0 : 2);
	}
}
