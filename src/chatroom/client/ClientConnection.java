package chatroom.client;

import chatroom.client.ui.IInterface;
import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Message;
import chatroom.shared.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ClientConnection
{
	private final ClientIdentity identity;
	private final IInterface ui;

	private BufferedReader in;
	private BufferedWriter out;

	public ClientConnection(ClientIdentity identity, IInterface ui)
	{
		this.identity = identity;
		this.ui = ui;
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

			// register signal handler
			Runtime.getRuntime().addShutdownHook(new Thread(this::disconnect));

			// request user list
			sendList();

			// request history
			sendHistory();

			// start receiving thread
			startReceivingThread();

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
			CommandQuit command = new CommandQuit(identity.getUsername());
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
		Message m = new Message(identity.getUsername(), message);
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
		CommandJoin command = new CommandJoin(identity.getUsername());
		command.send(out);

		String ack = CommandAck.readAck(in);
		if (ack != null)
		{
			ui.display(String.format("Error while connecting: %s", ack));
			return;
		}

		// read banner
		String banner = command.read(in);
		ui.display(String.format("The server says: %s", banner));
	}

	private void sendList() throws ChatException
	{
		CommandList command = new CommandList(identity.getUsername());
		command.send(out);

		String list = command.read(in);
		ui.display(list);
	}

	private void sendHistory() throws ChatException
	{
		CommandClientHistory command = new CommandClientHistory(identity.getUsername());
		command.send(out);

		List<Message> messages = command.readAndParse(in);
		messages.forEach(this::onReceiveMessage);
	}

	private void startReceivingThread()
	{
		Thread thread = new Thread(new ClientMessageReceiver(in, this::onReceiveMessage));
		thread.setDaemon(true);
		thread.start();
	}

	private void onReceiveMessage(Message m)
	{
		ui.displayMessage(m);
	}


	private class ClientMessageReceiver implements Runnable
	{
		private final BufferedReader instream;
		private final Consumer<Message> onReceiveMessage;

		ClientMessageReceiver(BufferedReader instream, Consumer<Message> onReceiveMessage)
		{
			this.instream = instream;
			this.onReceiveMessage = onReceiveMessage;
		}

		@Override
		public void run()
		{
			// wait for messages
			while (true)
			{
				// TODO needs to be interrupted on disconnect

				try
				{
					RequestPrologue request = Command.readPrologue(instream, Opcode.SEND);

					String encodedMessage = CommandSend.readMessageContent(instream);
					Message m = new Message(request.getUsername(), encodedMessage);
					m.decode();

					onReceiveMessage.accept(m);
				} catch (ChatException e)
				{
					e.printStackTrace();

					if (e.isSerious())
						break;
				}
			}

		}
	}

}
