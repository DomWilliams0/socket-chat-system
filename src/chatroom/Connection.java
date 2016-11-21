package chatroom;

import java.io.*;
import java.net.Socket;

public class Connection
{
	public enum Opcode
	{
		JOIN,
		SEND,
		QUIT;

		public String serialise()
		{
			return this.toString();
		}

		public static Opcode parse(String s)
		{
			try
			{
				return Opcode.valueOf(s);
			} catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}

	private static final String DELIMITER = "\n";

	private final String username;

	private Socket socket;
	private boolean connected;

	private BufferedReader in;
	private BufferedWriter out;


	public Connection(String username)
	{
		this.username = username;
		this.connected = false;
	}

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
			sendJoin();

		} catch (IOException e)
		{
			System.err.println("Could not connect to server: " + e.getMessage());
			success = false;
		}

		return success;
	}

	private void sendJoin() throws IOException
	{
		sendCommandPrologue(Opcode.JOIN);

		// read banner
		String banner = in.readLine();
		System.out.println("The server says: " + banner);
	}

	private void sendCommandPrologue(Opcode opcode)
	{
		try
		{
			ensureConnected();

			// start with opcode
			out.write(opcode.serialise());
			out.write(DELIMITER);

			// followed by username
			out.write(username);
			out.write(DELIMITER);

			out.flush();

			// followed by any opcode specific arguments
		} catch (IOException e)
		{
			Logger.error("Failed to send %s command: %s", opcode, e.getMessage());
		}
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

	public String getUsername()
	{
		return username;
	}

	public boolean isConnected()
	{
		return connected;
	}
}
