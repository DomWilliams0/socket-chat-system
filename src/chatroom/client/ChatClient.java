package chatroom.client;

import chatroom.Logger;
import chatroom.Protocol;
import chatroom.server.Message;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

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
		if (connected)
		{
			Logger.log("Disconnecting");
			sendCommandPrologue(Protocol.Opcode.QUIT);
			connected = false;
		}
	}

	public void sendMessage(String message)
	{
		sendCommandPrologue(Protocol.Opcode.SEND);

		// encode message in base64
		String encoded = Base64.getEncoder().encodeToString(message.getBytes());

		try
		{
			out.write(encoded);
			out.write("\n");
			out.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// no ack needed for now
	}

	private boolean sendCommandPrologue(Protocol.Opcode opcode)
	{
		ensureConnected();
		Protocol.RequestPrologue prologue = new Protocol.RequestPrologue(opcode, username);
		return Protocol.sendCommandPrologue(prologue, out);
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
			throw new IllegalStateException("Client is not connected to a server");
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

	public boolean start(String localhost, int port)
	{
		// connect to server
		if (!connect(localhost, port))
		{
			return false;
		}

		// register signal handler
		Runtime.getRuntime().addShutdownHook(new Thread(this::disconnect));

		// start receiving thread
		startReceivingThread();

		// send messages
		display("Type /quit to exit");
		Scanner scanner = new Scanner(System.in);
		String line;
		while ((line = scanner.nextLine()) != null)
		{
			// quit
			if (line.equals("/quit"))
			{
				break;
			}

			// empty
			if (line.isEmpty())
			{
				continue;
			}

			// send message
			sendMessage(line);
		}

		// disconnect
		disconnect();

		return true;
	}

	private void startReceivingThread()
	{
		Thread thread = new Thread(new ClientMessageReceiver(this));
		thread.setDaemon(true);
		thread.start();
	}

	public BufferedReader getIn()
	{
		return in;
	}

	public BufferedWriter getOut()
	{
		return out;
	}

	public void onReceiveMessage(Message m)
	{
		display("[%s]: %s", m.getFrom(), m.getContent());
	}

	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			Logger.error("Username expected");
			System.exit(1);
		}

		ChatClient client = new ChatClient(args[0]);
		boolean success = client.start("localhost", 6060);

		System.exit(success ? 0 : 1);
	}
}
