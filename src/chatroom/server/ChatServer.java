package chatroom.server;

import chatroom.Connection;
import chatroom.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer
{
	private String banner;
	private ServerSocket socket;

	// TODO choose listen ip
	public ChatServer(String banner)
	{
		if (banner == null)
		{
			banner = "";
		}

		this.banner = banner;
	}

	private void handleConnection(Socket client) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

		// read opcode
		String opcodeStr = in.readLine();
		Connection.Opcode opcode = Connection.Opcode.parse(opcodeStr);
		if (opcode == null)
		{
			Logger.error("Invalid opcode %s", opcodeStr);
			return;
		}

		// read username
		String username = in.readLine();
		Logger.log("User '%s' connected from %s", username, getClientAddress(client));

		// deal with opcode
		switch (opcode)
		{
			case JOIN:
				handleJoin(username, in, out);
				break;
			case SEND:
				handleSend(username, in, out);
				break;
			case QUIT:
				handleQuit(username, in, out);
				break;
		}
	}

	private void handleQuit(String username, BufferedReader in, BufferedWriter out)
	{
		// TODO
	}

	private void handleSend(String username, BufferedReader in, BufferedWriter out)
	{
		// TODO
	}

	private void handleJoin(String username, BufferedReader in, BufferedWriter out) throws IOException
	{
		// send banner
		out.write(banner);
		out.write("\n");
		out.flush();

		// TODO ensure client username is unique
		// TODO add client to client list
	}

	public boolean listen(int port)
	{
		try
		{
			socket = new ServerSocket(port);

		} catch (IOException e)
		{
			Logger.error("Failed to listen on port %d: %s", port, e.getMessage());
			return false;
		}

		Logger.log("Listening on port %d...", port);

		Socket client = null;

		boolean running = true;
		while (running)
		{
			try
			{
				// TODO extract to ServerConnection?
				client = socket.accept();
				handleConnection(client);
				Logger.log("Client disconnected from %s", getClientAddress(client));
			} catch (IOException e)
			{
				Logger.error("Failed to communicate with client %s: %s", getClientAddress(client), e.getMessage());
			} finally
			{
				if (client != null && !client.isClosed())
				{
					try
					{
						client.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}


		// close server socket
		if (socket != null)
		{
			try
			{
				socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return true;
	}

	private String getClientAddress(Socket socket)
	{
		return socket.getRemoteSocketAddress().toString();
	}

	public static void main(String[] args)
	{
		ChatServer server = new ChatServer("Welcome!");
		server.listen(6060);
	}
}
