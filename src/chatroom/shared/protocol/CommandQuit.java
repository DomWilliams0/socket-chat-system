package chatroom.shared.protocol;

/**
 * Represents a quit command
 */
public class CommandQuit extends Command
{
	public CommandQuit(String username)
	{
		super(Opcode.QUIT, username);
	}
}
