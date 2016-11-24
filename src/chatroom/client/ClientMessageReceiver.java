package chatroom.client;

import chatroom.server.Message;
import chatroom.shared.ChatException;
import chatroom.shared.protocol.Command;
import chatroom.shared.protocol.CommandSend;
import chatroom.shared.protocol.Opcode;
import chatroom.shared.protocol.RequestPrologue;

import java.io.BufferedReader;

class ClientMessageReceiver implements Runnable
{
	private final ChatClient client;

	ClientMessageReceiver(ChatClient client)
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

			try
			{
				RequestPrologue request = Command.readPrologue(in, Opcode.SEND);

				String encodedMessage = CommandSend.readMessageContent(in);
				Message m = new Message(request.getUsername(), encodedMessage);
				m.decode();

				client.onReceiveMessage(m);
			} catch (ChatException e)
			{
				e.printStackTrace();

				if (e.isSerious())
					break;
			}
		}

	}
}
