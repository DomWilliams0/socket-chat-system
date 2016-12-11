package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.protocol.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection
{
	private ServerSocket socket;
	private ChatServer server;

	public ServerConnection(ChatServer server)
	{
		this.server = server;
	}

	static void sendMessageToClient(ClientInstance client, Message message)
	{
		try
		{

			CommandSend command = new CommandSend(message);
			command.send(client.getOut());

		} catch (ChatException e)
		{
			e.printStackTrace();
		}
	}

	public boolean bind(String iface, int port)
	{
		try
		{
			socket = new ServerSocket(port, 0, InetAddress.getByName(iface));
			return true;

		} catch (IOException e)
		{
			Logger.error("Failed to bind to %s:%d: %s", iface, port, e.getMessage());
			return false;
		}
	}

	private void handleConnection(Socket client) throws ChatException
	{
		try
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			RequestPrologue prologue = Command.readPrologue(in, Opcode.JOIN);
			Logger.log("User '%s' connected from %s", prologue.getUsername(), getClientAddress(client));

			handleJoin(prologue.getUsername(), in, out);

		} catch (IOException e)
		{
			throw new ChatException(e);
		}
	}

	private void handleJoin(String username, BufferedReader in, BufferedWriter out) throws ChatException
	{
		ChatException error = null;

		// acknowledge
		try
		{
			server.addClient(username, in, out);
		} catch (ChatException e)
		{
			error = e;
		}

		CommandAck ack = new CommandAck(error);
		ack.send(out);

		// error; abort
		if (error != null)
			return;

		// send banner
		CommandJoin.sendBanner(out, server.getBanner());
	}

	private boolean acceptClient()
	{
		Socket client = null;
		boolean maintainConnection = true;

		try
		{
			client = socket.accept();
			handleConnection(client);
		} catch (IOException e)
		{
			Logger.error("Failed to handle client: %s", e.getMessage());
			return false;
		} catch (ChatException e)
		{
			e.printStackTrace();
			maintainConnection = false;
		}

		if (!maintainConnection)
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

		return true;
	}

	public boolean startListening(String iface, int port)
	{
		// bind to port
		if (!bind(iface, port))
		{
			return false;
		}

		Logger.log("Listening on %s:%d", iface, port);

		boolean running = true;
		while (running)
		{
			acceptClient();
		}

		shutdown();
		return true;
	}

	public void shutdown()
	{
		Logger.log("Shutting down server");
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
	}

	private String getClientAddress(Socket socket)
	{
		return socket.getRemoteSocketAddress().toString();
	}

}
