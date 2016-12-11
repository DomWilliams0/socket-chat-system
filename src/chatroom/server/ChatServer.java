package chatroom.server;

import java.util.Arrays;

public class ChatServer
{
	private final ServerConnection connection;

	public ChatServer(ServerState state)
	{
		this.connection = new ServerConnection(state);
	}

	public static void main(String[] args)
	{
		if (args.length < 3)
			throw new IllegalArgumentException("Usage: <interface> <port> <banner>");

		String iface = args[0];
		Integer port = Integer.parseInt(args[1]);

		String[] bannerArgs = Arrays.copyOfRange(args, 2, args.length);
		String serverBanner = String.join(" ", bannerArgs);

		ServerState state = new ServerState(serverBanner);
		ChatServer server = new ChatServer(state);

		boolean success = server.start(iface, port);

		System.exit(success ? 0 : 1);
	}

	private boolean start(String iface, Integer port)
	{
		return connection.startListening(iface, port);
	}
}
