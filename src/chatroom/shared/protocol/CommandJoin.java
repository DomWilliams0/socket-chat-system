package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedWriter;

/**
 * Represents a join command
 */
public class CommandJoin extends Command
{
	public CommandJoin(String username)
	{
		super(Opcode.JOIN, username);
	}

	/**
	 * Writes the given banner to the given writer
	 *
	 * @param out    The writer
	 * @param banner The banner to write
	 */
	public static void sendBanner(BufferedWriter out, String banner) throws ChatException
	{
		sendArgument(out, banner);
	}
}
