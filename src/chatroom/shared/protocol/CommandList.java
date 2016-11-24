package chatroom.shared.protocol;

import chatroom.server.ChatServer;
import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Collection;

public class CommandList extends Command
{
	public CommandList(String username)
	{
		super(Opcode.LIST, username);
	}

	@Override
	public String read(BufferedReader in) throws ChatException
	{
		int count = Integer.parseInt(readArgument(in));
		if (count < 0)
			throw new ChatException("Negative user count");

		StringBuilder sb = new StringBuilder();
		sb.append(count).append(" user(s) online");

		if (count > 0)
		{
			sb.append(": ").append(readArgument(in));

			for (int i = 1; i < count; i++)
				sb.append(", ").append(readArgument(in));
		}

		return sb.toString();
	}

	public static void sendUserList(BufferedWriter out, ChatServer server) throws ChatException
	{
		sendArgument(out, Integer.toString(server.getUserCount()));
		sendArgument(out, server.getUserList(Protocol.DELIMITER));
	}
}
