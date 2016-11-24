package chatroom.shared.protocol;

public class CommandClientJoin extends Command
{
	public CommandClientJoin(String username)
	{
		super(Opcode.JOIN, username);
	}
}
