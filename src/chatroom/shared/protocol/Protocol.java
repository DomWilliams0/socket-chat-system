package chatroom.shared.protocol;

import chatroom.shared.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Base64;

public class Protocol
{
	public static final String DELIMITER = "\n";
	public static final String SERVER_USERNAME = "SERVER";

	@Deprecated
	public static boolean sendCommandPrologue(RequestPrologue prologue, Writer out)
	{
		try
		{
			// start with opcode
			out.write(prologue.getOpcode().serialise());
			out.write(DELIMITER);

			// followed by username
			out.write(prologue.getUsername());
			out.write(DELIMITER);

			out.flush();

			// followed by any opcode specific arguments
		} catch (IOException e)
		{
			Logger.error("Failed to send %s request: %s", prologue.getOpcode(), e.getMessage());
			return false;
		}

		return true;
	}

	@Deprecated
	public static RequestPrologue readCommandPrologue(BufferedReader reader, Opcode... expectedOpcodes)
	{
		try
		{
			// read opcode
			String opcodeStr = reader.readLine();
			if (opcodeStr == null)
				throw new IllegalArgumentException("Read error");

			// parse opcode
			Opcode opcode = Opcode.parse(opcodeStr);
			if (opcode == null)
				throw new IllegalArgumentException("Invalid opcode '" + opcodeStr + "'");

			// validate opcode
			if (expectedOpcodes != null && !Arrays.asList(expectedOpcodes).contains(opcode))
				throw new IllegalArgumentException("Unexpected opcode " + opcode);

			// read username
			String username = reader.readLine();

			return new RequestPrologue(opcode, username);

		} catch (IOException | IllegalArgumentException e)
		{
			Logger.error(e.getMessage());
			return null;
		}
	}

	public static String encodeMessage(String plaintext)
	{
		return Base64.getEncoder().encodeToString(plaintext.getBytes());
	}

	public static String decodeMessage(String encoded)
	{
		try
		{
			return new String(Base64.getDecoder().decode(encoded));
		} catch (IllegalArgumentException e)
		{
			Logger.error("Failed to decode message: %s", e.getMessage());
			return "<INVALID MESSAGE>";
		}
	}

}
