package chatroom.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;

class ClientInstance
{
	private final String username;
	private final BufferedReader in;
	private final BufferedWriter out;
	private final Thread thread;

	ClientInstance(String username, BufferedReader in, BufferedWriter out, ServerState serverInstance)
	{
		this.username = username;
		this.in = in;
		this.out = out;

		this.thread = new Thread(new ServerMessageReceiver(this, serverInstance));
		this.thread.start();
	}

	public String getUsername()
	{
		return username;
	}

	public BufferedReader getIn()
	{
		return in;
	}

	public BufferedWriter getOut()
	{
		return out;
	}

	public Thread getThread()
	{
		return thread;
	}

	public void stop()
	{
		// TODO interrupt?
		throw new RuntimeException("Not implemented");
	}
}
