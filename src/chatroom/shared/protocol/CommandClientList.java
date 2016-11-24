package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedReader;

public class CommandClientList extends Command
{
	public CommandClientList(String username)
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
}
