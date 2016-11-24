package chatroom.client;

import chatroom.server.Message;
import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ChatClient
{
	private final String username;

	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * @param username The client's username to use in the chatroom
	 */
	public ChatClient(String username)
	{
		this.username = username;

		if (username == null ||
			username.length() < 3 ||
			username.contains(Protocol.DELIMITER) ||
			username.equals(Protocol.SERVER_USERNAME))
			throw new IllegalArgumentException("Invalid username");
	}

	public static boolean runClient(String address, int port, String username)
	{
		ChatClient client = new ChatClient(username);
		return client.start(address, port);
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
		boolean success = false;
		try
		{
			String addrStr = address + ":" + port;

			Logger.log(String.format("Attempting to connect to %s...", addrStr));
			Socket socket = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			Logger.log("Successfully connected");

			// send join command
			sendJoin();

			// request user list
			sendList();

			// request history
			sendHistory();

			success = true;
		} catch (IOException e)
		{
			Logger.error("Could not connect to server: %s", e.getMessage());
		} catch (ChatException e)
		{
			e.printStackTrace();
		}

		return success;
	}

	public void disconnect()
	{
		if (out == null && in == null)
			return;

		try
		{
			Logger.log("Disconnecting");
			CommandQuit command = new CommandQuit(username);
			command.send(out);

			out = null;
			in = null;

		} catch (ChatException e)
		{
			e.printStackTrace();
		}
	}

	public void sendMessage(String message)
	{
		Message m = new Message(username, message);
		m.encode();

		CommandSend command = new CommandSend(m);
		try
		{
			command.send(out);
		} catch (ChatException e)
		{
			e.printStackTrace();
		}

		// no ack needed for now
	}

	private void sendJoin() throws ChatException
	{
		CommandJoin command = new CommandJoin(username);
		command.send(out);

		String ack = CommandAck.readAck(in);
		if (ack != null)
		{
			display("Error while connecting: %s", ack);
			return;
		}

		// read banner
		String banner = command.read(in);
		display("The server says: %s", banner);
	}

	private void sendList() throws ChatException
	{
		CommandList command = new CommandList(username);
		command.send(out);

		String list = command.read(in);
		display(list);
	}

	private void sendHistory() throws ChatException
	{
		CommandClientHistory command = new CommandClientHistory(username);
		command.send(out);

		List<Message> messages = command.readAndParse(in);
		messages.forEach(this::onReceiveMessage);
	}

	/**
	 * @return The client's username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Displays the given message on the UI
	 */
	private void display(String message, Object... format)
	{
		System.out.printf(message + "\n", format);
	}

	public boolean start(String address, int port)
	{
		// connect to server
		if (!connect(address, port))
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
}
