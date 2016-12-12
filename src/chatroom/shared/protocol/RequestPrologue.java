package chatroom.shared.protocol;

/**
 * Represents the common prologue to all commands
 */
public class RequestPrologue
{
	private final Opcode opcode;
	private final String username;

	/**
	 * @param opcode   The command opcode
	 * @param username The command sender's username
	 */
	public RequestPrologue(Opcode opcode, String username)
	{
		this.opcode = opcode;
		this.username = username;
	}

	/**
	 * @return The command opcode
	 */
	public Opcode getOpcode()
	{
		return opcode;
	}

	/**
	 * @return The command sender's username
	 */
	public String getUsername()
	{
		return username;
	}
}
