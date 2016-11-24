package chatroom.shared.protocol;

public class CommandQuit extends Command
{
	public CommandQuit(String username)
	{
		super(Opcode.QUIT, username);
	}
}
