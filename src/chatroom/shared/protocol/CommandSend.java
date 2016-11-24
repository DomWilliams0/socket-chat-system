package chatroom.shared.protocol;

import chatroom.server.Message;
import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class CommandSend extends Command
{
	private final Message message;

	public CommandSend(Message message)
	{
		super(Opcode.SEND, message.getFrom());
		this.message = message;
	}

	@Override
	public void send(BufferedWriter out) throws ChatException
	{
		super.send(out);
		sendArgument(out, message.getContent());
	}

	public static Message readMessageFromClient(BufferedReader in, String sender) throws ChatException
	{
		return new Message(sender, readMessageContent(in));
	}

	public static String readMessageContent(BufferedReader in) throws ChatException
	{
		return readArgument(in);
	}
}