package chatroom.shared.protocol;

public class RequestPrologue
{
	private final Opcode opcode;
	private final String username;

	public RequestPrologue(Opcode opcode, String username)
	{
		this.opcode = opcode;
		this.username = username;
	}

	public Opcode getOpcode()
	{
		return opcode;
	}

	public String getUsername()
	{
		return username;
	}
}
