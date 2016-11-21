package chatroom;

import java.io.IOException;
import java.io.Writer;

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

	public static boolean sendCommandPrologue(Opcode opcode, String username, Writer out)
	{
		try
		{
			// start with opcode
			out.write(opcode.serialise());
			out.write(DELIMITER);

			// followed by username
			out.write(username);
			out.write(DELIMITER);

			out.flush();

			// followed by any opcode specific arguments
		} catch (IOException e)
		{
			Logger.error("Failed to send %s command: %s", opcode, e.getMessage());
			return false;
		}

		return true;
	}
}
