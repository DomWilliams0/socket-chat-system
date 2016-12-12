package chatroom.server;

import chatroom.shared.Logger;

import java.util.Arrays;

public class ChatServer
{
	private final ServerConnection connection;

	/**
	 * @param state The server state
	 */
	public ChatServer(ServerState state)
	{
		this.connection = new ServerConnection(state);
	}

	public static void main(String[] args)
	{
		boolean success;

		try
		{
			if (args.length < 3)
				throw new IllegalArgumentException("Usage: <interface> <port> <banner>");

			String iface = args[0];
			Integer port = Integer.parseInt(args[1]);

			String[] bannerArgs = Arrays.copyOfRange(args, 2, args.length);
			String serverBanner = String.join(" ", bannerArgs);

			ServerState state = new ServerState(serverBanner);
			ChatServer server = new ChatServer(state);

			success = server.start(iface, port);

		} catch (IllegalArgumentException e)
		{
			Logger.error(e.getMessage());
			success = false;
		}

		System.exit(success ? 0 : 1);
	}

	/**
	 * Starts the server on the given address
	 *
	 * @param iface The interface to listen on
	 * @param port  The port to listen on
	 * @return If the server started up properly
	 */
	private boolean start(String iface, Integer port)
	{
		return connection.startListening(iface, port);
	}
}
