package chatroom.server;

import chatroom.Logger;
import chatroom.Protocol;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientConnection
{
	private final String username;
	private final BufferedReader in;
	private final BufferedWriter out;
	private final Thread thread;

	private boolean running;

	public ClientConnection(String username, BufferedReader in, BufferedWriter out)
	{
		this.username = username;
		this.in = in;
		this.out = out;
		this.running = true;
		thread = new Thread(() ->
		{

			// wait for messages
			while (running)
			{
				// TODO needs to be interrupted

				// read opcode
				String opcodeStr = null;
				try
				{
					// read SEND opcode
					opcodeStr = in.readLine();
					if (!opcodeStr.equals(Protocol.Opcode.SEND.serialise()))
					{
						Logger.error("Expected message opcode, got '%s' instead", opcodeStr);
						continue;
					}

					// read username and ensure it's correct
					String usernameStr = in.readLine();
					if (!username.equals(usernameStr))
					{
						Logger.error("User '%s' tried sending a message as '%s', uh oh", username, usernameStr);
						continue;
					}

					// read message
					String encoded = in.readLine();
					String decoded = new String(Base64.decode(encoded));

					System.out.println("decoded = " + decoded);

					// TODO decode and process message

				} catch (IOException e)
				{
					e.printStackTrace();
					return;
				} catch (Base64DecodingException e)
				{
					Logger.error("Badly encoded message received");
					return;
				}

			}

		});

		thread.start();
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
}
