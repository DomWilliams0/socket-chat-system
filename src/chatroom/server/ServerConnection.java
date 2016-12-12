package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Message;
import chatroom.shared.protocol.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Represents networking for the server
 */
public class ServerConnection
{
	private ServerSocket socket;
	private ServerState server;

	/**
	 * @param server The server
	 */
	public ServerConnection(ServerState server)
	{
		this.server = server;
	}

	/**
	 * Sends a message to the given client
	 *
	 * @param client  The client to send the message to
	 * @param message The message to send
	 */
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

	/**
	 * Attempts to bind to the given address
	 *
	 * @param iface The interface to bind to
	 * @param port  The port to bind to
	 * @return If binding was successful
	 */
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

	/**
	 * Reads and handles join commands from the given client socket
	 *
	 * @param client A client's spanking new socket
	 */
	private void handleConnection(Socket client) throws ChatException
	{
		try
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			RequestPrologue prologue = Command.readPrologue(in, Opcode.JOIN);

			if (prologue != null)
			{
				Logger.log("User '%s' connected from %s", prologue.getUsername(), getClientAddress(client));
				handleJoin(prologue.getUsername(), in, out);
			}

		} catch (IOException e)
		{
			throw new ChatException(e);
		}
	}

	/**
	 * Handles join commands from the given client
	 *
	 * @param username The client's username
	 * @param in       The client's input stream
	 * @param out      The client's output stream
	 */
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
			throw error;

		// send banner
		CommandJoin.sendBanner(out, server.getBanner());
	}

	/**
	 * Blocks until a client connects, and closes the socket when they disconnect
	 *
	 * @return If handling the client was a success
	 */
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

	/**
	 * Bind to and serve on the given address
	 *
	 * @param iface The interface to bind to
	 * @param port  The port to bind to
	 * @return If binding was successful
	 */
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

	/**
	 * Close the server socket
	 */
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

	/**
	 * @param socket A client
	 * @return String representation of the client's address
	 */
	private String getClientAddress(Socket socket)
	{
		return socket.getRemoteSocketAddress().toString();
	}

}
