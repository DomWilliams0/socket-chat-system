package chatroom.shared.protocol;

public class CommandClientQuit extends Command
{
	public CommandClientQuit(String username)
	{
		super(Opcode.QUIT, username);
	}
}
