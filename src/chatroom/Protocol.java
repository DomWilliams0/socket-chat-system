package chatroom;

public class Protocol
{
	public enum Opcode
	{
		JOIN,
		SEND,
		QUIT,

		SUCC, // success
		ERRO; // error + message

		public String serialise()
		{
			return this.toString();
		}

		public static Opcode parse(String s)
		{
			try
			{
				return Opcode.valueOf(s);
			} catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}

	public static final String DELIMITER = "\n";

}
