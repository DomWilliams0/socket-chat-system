package chatroom.client;

import chatroom.Logger;
import chatroom.Protocol;
import chatroom.server.Message;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientMessageReceiver implements Runnable
{
	private final ChatClient client;

	public ClientMessageReceiver(ChatClient client)
	{
		this.client = client;
	}

	@Override
	public void run()
	{
		// wait for messages
		while (true)
		{
			// TODO needs to be interrupted on disconnect

			BufferedReader in = client.getIn();
			String username = client.getUsername();

			// TODO this is all very similar to the server message receiver - unacceptable!

			// read opcode
			String opcodeStr;
			try
			{
				// read opcode
				opcodeStr = in.readLine();

				if (opcodeStr == null)
				{
					Logger.error("Read error");
					return;
				}

				// validate opcode
				Protocol.Opcode opcode = Protocol.Opcode.parse(opcodeStr);
				if (opcode != Protocol.Opcode.SEND)
				{
					Logger.error("Expected send opcode, but received", opcodeStr);
					continue;
				}

				// read sender and message
				String sender = in.readLine();
				String encodedMessage = in.readLine();

				Message m = new Message(sender, encodedMessage);
				m.decode();

				client.onReceiveMessage(m);

			} catch (IOException e)
			{
				Logger.error(e.getMessage());
				return;
			}

		}

	}
}
