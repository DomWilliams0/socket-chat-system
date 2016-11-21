package chatroom;

public class Protocol
{
	public enum Opcode
	{
		JOIN,
		SEND,
		QUIT;

		public String serialise()
		{
			return this.toString();
		}

		public static Opcode parse(String s)
		{
			// TODO test
			return Opcode.valueOf(s);
		}
	}

	public static final String DELIMITER = "\n";



}
