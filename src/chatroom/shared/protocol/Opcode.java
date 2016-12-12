package chatroom.shared.protocol;

/**
 * Represents an opcode sent between clients and the server
 */
public enum Opcode
{
	JOIN, // join the server
	SEND, // send a message
	QUIT, // quit the server

	// TODO
	LIST, // list online users
	HIST, // get message history

	SUCC, // success
	ERRO; // error + message

	/**
	 * @param s The string to parse to an opcode
	 * @return The corresponding opcode, or null if not found
	 */
	public static Opcode parse(String s)
	{
		try
		{
			return Opcode.valueOf(s);
		} catch (RuntimeException e)
		{
			return null;
		}
	}

	/**
	 * @return The string representation of this opcode to send over the network
	 */
	public String serialise()
	{
		return this.toString();
	}
}
