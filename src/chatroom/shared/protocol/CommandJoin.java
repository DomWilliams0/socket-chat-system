package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedWriter;

public class CommandJoin extends Command
{
	public CommandJoin(String username)
	{
		super(Opcode.JOIN, username);
	}

	public static void sendBanner(BufferedWriter out, String banner) throws ChatException
	{
		sendArgument(out, banner);
	}
}
