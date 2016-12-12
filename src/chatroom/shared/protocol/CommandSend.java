package chatroom.shared.protocol;

import chatroom.shared.ChatException;
import chatroom.shared.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Represents a command to send a message
 */
public class CommandSend extends Command
{
	private final Message message;

	/**
	 * @param message The message to send
	 */
	public CommandSend(Message message)
	{
		super(Opcode.SEND, message.getFrom());
		this.message = message;
	}

	/**
	 * Read and parse a message from the given reader
	 *
	 * @param in     The reader
	 * @param sender The message sender
	 * @return The parsed message
	 */
	public static Message readMessageFromClient(BufferedReader in, String sender) throws ChatException
	{
		return new Message(sender, readMessageContent(in));
	}

	/**
	 * Reads message content from the given reader
	 *
	 * @param in The reader
	 * @return The message content
	 */
	public static String readMessageContent(BufferedReader in) throws ChatException
	{
		return readArgument(in);
	}

	@Override
	public void send(BufferedWriter out) throws ChatException
	{
		super.send(out);
		sendArgument(out, message.getContent());
	}
}
