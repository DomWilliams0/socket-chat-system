package chatroom.client;

import java.io.*;
import java.net.Socket;

public class ChatClient
{
	private String username;

	private Socket socket;
	private boolean connected;

	private BufferedReader sockIn;
	private BufferedWriter sockOut;

	/**
	 * @param username The client's username to use in the chatroom
	 */
	public ChatClient(String username)
	{
		this.username = username;
		this.socket = null;
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

			log(String.format("Attempting to connect to %s...", addrStr));
			socket = new Socket(address, port);
			sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sockOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			success = true;

			log("Successfully connected");

			// TODO send join command

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

	/**
	 * Throws an IllegalStateException if the client is not connected to a server
	 */
	private void ensureConnected()
	{
		if (!isConnected())
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
	 * Helper method for consistent error logging
	 */
	private void error(String message, Object... format)
	{
		System.err.printf("[ERR] " + message + "\n", format);
	}

	/**
	 * Helper method for consistent logging
	 */
	private void log(String message, Object... format)
	{
		System.out.printf("[LOG] " + message + "\n", format);
	}

	public static void main(String[] args)
	{
		ChatClient client = new ChatClient("user");
		client.connect("localhost", 6060);
	}
}
