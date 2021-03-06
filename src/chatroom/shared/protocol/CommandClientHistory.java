package chatroom.shared.protocol;

import chatroom.server.ServerState;
import chatroom.shared.ChatException;
import chatroom.shared.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chat history request
 */
public class CommandClientHistory extends Command
{
	public CommandClientHistory(String username)
	{
		super(Opcode.HIST, username);
	}

	/**
	 * Writes the given server's full chat history to the given writer
	 *
	 * @param out            The writer
	 * @param serverInstance The server
	 */
	public static void sendChatHistory(BufferedWriter out, ServerState serverInstance) throws ChatException
	{
		List<Message> history = serverInstance.getMessageHistory();
		int historySize = history.size();

		sendArgument(out, Integer.toString(historySize));

		for (Message message : history)
		{
			sendArgument(out, message.getFrom());
			sendArgument(out, message.getContent());
		}
	}

	/**
	 * Reads chat history from the given reader and parses into separate messages
	 *
	 * @param in The reader
	 * @return List of parsed messages
	 */
	public List<Message> readAndParse(BufferedReader in) throws ChatException
	{
		List<Message> messages = new ArrayList<>();

		int count = Integer.parseInt(readArgument(in));
		if (count < 0)
			throw new ChatException("Negative history length");

		for (int i = 0; i < count; i++)
		{
			Message m = new Message(readArgument(in), readArgument(in));
			m.decode();
			messages.add(m);
		}

		return messages;
	}
}
