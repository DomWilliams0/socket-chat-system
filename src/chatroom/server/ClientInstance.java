package chatroom.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Represents a single client connected to the server
 */
class ClientInstance
{
	private final String username;
	private final BufferedReader in;
	private final BufferedWriter out;

	/**
	 * @param username       The client's username
	 * @param in             The stream to read client input from
	 * @param out            The stream to write to
	 * @param serverInstance The server
	 */
	ClientInstance(String username, BufferedReader in, BufferedWriter out, ServerState serverInstance)
	{
		this.username = username;
		this.in = in;
		this.out = out;

		Thread thread = new Thread(new ServerMessageReceiver(this, serverInstance));
		thread.start();
	}

	/**
	 * @return This client's username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return This client's input stream
	 */
	public BufferedReader getIn()
	{
		return in;
	}

	/**
	 * @return This client's output stream
	 */
	public BufferedWriter getOut()
	{
		return out;
	}

}
