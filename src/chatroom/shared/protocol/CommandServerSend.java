package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedWriter;

public class CommandServerSend extends Command
{
	private final String message;

	public CommandServerSend(String username, String encodedMessage)
	{
		super(Opcode.SEND, username);
		this.message = encodedMessage;
	}

	@Override
	public void send(BufferedWriter out) throws ChatException
	{
		super.send(out);

		sendArgument(message, out);
	}
}
