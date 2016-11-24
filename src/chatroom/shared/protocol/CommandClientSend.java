package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedWriter;

public class CommandClientSend extends Command
{
	private final String message;

	public CommandClientSend(String username, String message)
	{
		super(Opcode.SEND, username);
		this.message = Protocol.encodeMessage(message);
	}

	@Override
	public void send(BufferedWriter out) throws ChatException
	{
		super.send(out);

		sendArgument(message, out);
	}
}
