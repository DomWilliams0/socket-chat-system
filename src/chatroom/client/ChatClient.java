package chatroom.client;

import chatroom.Logger;
import chatroom.Protocol;

import java.io.*;
import java.net.Socket;

public class ChatClient
{
	private final String username;

	private Socket socket;
	private boolean connected;

	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * @param username The client's username to use in the chatroom
	 */
	public ChatClient(String username)
	{
		this.username = username;
		this.connected = false;

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
		boolean success;
		try
		{
			String addrStr = address + ":" + port;

			Logger.log(String.format("Attempting to connect to %s...", addrStr));
			socket = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			success = true;

			connected = true;
			Logger.log("Successfully connected");

			// join
			success = sendJoin();

		} catch (IOException e)
		{
			System.err.println("Could not connect to server: " + e.getMessage());
			success = false;
		}

		return success;
	}


	public void disconnect()
	{
		// TODO
	}

	private boolean sendCommandPrologue(Protocol.Opcode opcode)
	{
		try
		{
			ensureConnected();

			// start with opcode
			out.write(opcode.serialise());
			out.write(Protocol.DELIMITER);

			// followed by username
			out.write(username);
			out.write(Protocol.DELIMITER);

			out.flush();

			// followed by any opcode specific arguments
		} catch (IOException e)
		{
			Logger.error("Failed to send %s command: %s", opcode, e.getMessage());
			return false;
		}

		return true;
	}

	private String readAck()
	{
		try
		{
			ensureConnected();

			String opcodeStr = in.readLine();
			Protocol.Opcode opcode = Protocol.Opcode.parse(opcodeStr);

			// success, phew
			if (opcode == Protocol.Opcode.SUCC)
			{
				return null;
			}

			// error, oh dear
			if (opcode == Protocol.Opcode.ERRO)
			{
				return in.readLine();
			}

			// something else
			return "Invalid ack opcode '" + opcodeStr + "'";


		} catch (IOException e)
		{
			return "Failed to read ack: " + e.getMessage();
		}
	}

	private boolean sendJoin() throws IOException
	{
		// send join command
		sendCommandPrologue(Protocol.Opcode.JOIN);

		// read ack
		String ack = readAck();

		// error
		if (ack != null)
		{
			display("Error while connecting: %s", ack);
			return false;
		}


		// read banner
		String banner = in.readLine();
		display("The server says: %s", banner);

		return true;
	}

	/**
	 * Throws an IllegalStateException if the client is not connected to a server
	 */
	private void ensureConnected()
	{
		if (!connected)
		{
			throw new IllegalStateException("Client is not connected to a server");
		}
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
		return connected;
	}

	/**
	 * Displays the given message on the UI
	 */
	private void display(String message, Object... format)
	{
		System.out.printf(message + "\n", format);
	}

	public static void main(String[] args)
	{
		ChatClient client = new ChatClient("user");
		boolean success = client.connect("localhost", 6060);
		System.out.println("success = " + success);
	}
}
