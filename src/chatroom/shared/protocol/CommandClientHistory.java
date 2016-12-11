package chatroom.shared.protocol;

import chatroom.server.ChatServer;
import chatroom.server.Message;
import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class CommandClientHistory extends Command
{
	public CommandClientHistory(String username)
	{
		super(Opcode.HIST, username);
	}

	public static void sendChatHistory(BufferedWriter out, ChatServer serverInstance) throws ChatException
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
