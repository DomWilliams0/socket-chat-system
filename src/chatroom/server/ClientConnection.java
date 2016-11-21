package chatroom.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ClientConnection
{
	private final String username;
	private final BufferedReader in;
	private final BufferedWriter out;

	public ClientConnection(String username, BufferedReader in, BufferedWriter out)
	{
		this.username = username;
		this.in = in;
		this.out = out;
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
}
