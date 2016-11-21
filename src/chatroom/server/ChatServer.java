package chatroom.server;

public class ChatServer
{
	private final String banner;
	// TODO client list

	public ChatServer(String banner)
	{
		if (banner == null)
		{
			banner = "";
		}

		this.banner = banner;
	}

	public String getBanner()
	{
		return banner;
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
