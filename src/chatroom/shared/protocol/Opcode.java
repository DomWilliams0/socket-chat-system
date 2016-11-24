package chatroom.shared.protocol;

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

	public String serialise()
	{
		return this.toString();
	}
}
