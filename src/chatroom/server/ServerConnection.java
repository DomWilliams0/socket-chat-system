package chatroom.server;

import chatroom.ChatException;
import chatroom.Logger;
import chatroom.Protocol;

import java.io.*;
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

	public boolean bind(int port)
	{
		try
		{
			socket = new ServerSocket(port);
			return true;

		} catch (IOException e)
		{
			Logger.error("Failed to bind to port %d: %s", port, e.getMessage());
			return false;
		}
	}

	private void handleConnection(Socket client) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

		// read opcode
		String opcodeStr = in.readLine();
		Protocol.Opcode opcode = Protocol.Opcode.parse(opcodeStr);
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
		// TODO this should not happen
	}

	private void handleJoin(String username, BufferedReader in, BufferedWriter out) throws IOException
	{
		String error = null;

		// send ack
		try
		{
			server.addClient(username, in, out);
		} catch (ChatException e)
		{
			error = e.getMessage();
		}

		sendResponse(error, out);

		// error; abort
		if (error != null)
		{
			return;
		}

		// send banner
		out.write(server.getBanner() + Protocol.DELIMITER);
		out.flush();
	}

	private void sendResponse(String error, BufferedWriter out) throws IOException
	{
		// success
		if (error == null)
		{
			out.write(Protocol.Opcode.SUCC.serialise());
			out.write(Protocol.DELIMITER);
		}

		// error
		else
		{
			out.write(Protocol.Opcode.ERRO.serialise());
			out.write(Protocol.DELIMITER);

			out.write(error);
			out.write(Protocol.DELIMITER);
		}

		out.flush();
	}


	private boolean acceptClient()
	{
		Socket client = null;

		try
		{
			// TODO extract to ServerConnection?
			client = socket.accept();
			handleConnection(client);
		} catch (IOException e)
		{
			Logger.error("Failed to handle client: %s", e.getMessage());
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


		return true;
	}

	public boolean startListening(int port)
	{
		// bind to port
		if (!bind(port))
		{
			return false;
		}

		Logger.log("Listening on port %d", port);

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
