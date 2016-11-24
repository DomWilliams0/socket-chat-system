package chatroom.shared.protocol;


import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public abstract class Command
{
	protected final Opcode opcode;
	protected final String username;

	protected Command(Opcode opcode, String username)
	{
		this.opcode = opcode;
		this.username = username;
	}

	public static void sendArgument(String arg, BufferedWriter out) throws ChatException
	{
		try
		{
			out.write(arg);
			out.write(Protocol.DELIMITER);
			out.flush();
		} catch (IOException e)
		{
			throw new ChatException(e);
		}
	}

	public static String readArgument(BufferedReader in) throws ChatException
	{
		try
		{
			String arg = in.readLine();
			if (arg == null)
				throw new IOException("Stream closed");

			return arg;
		} catch (IOException e)
		{
			throw new ChatException(e);
		}
	}

	protected static Opcode readOpcode(BufferedReader in) throws ChatException
	{
		String opcodeStr = readArgument(in);

		Opcode opcode = Opcode.parse(opcodeStr);
		if (opcode == null)
			throw new ChatException("Invalid opcode '" + opcodeStr + "'");

		return opcode;
	}

	public static RequestPrologue readPrologue(BufferedReader in, Opcode... expectedOpcodes) throws ChatException, IllegalArgumentException
	{
		// read opcode
		Opcode opcode = readOpcode(in);

		// validate opcode
		if (expectedOpcodes != null && !Arrays.asList(expectedOpcodes).contains(opcode))
			throw new IllegalArgumentException("Unexpected opcode " + opcode);

		// read username
		String username = readArgument(in);

		return new RequestPrologue(opcode, username);
	}

	public void send(BufferedWriter out) throws ChatException
	{
		sendPrologue(out); // no extra args
	}

	public String read(BufferedReader in) throws ChatException
	{
		return readArgument(in);
	}

	protected void sendPrologue(BufferedWriter out) throws ChatException
	{
		// start with opcode
		sendArgument(opcode.serialise(), out);

		// followed by username
		sendArgument(username, out);

		// followed by any opcode specific arguments
	}

	public Opcode getOpcode()
	{
		return opcode;
	}
}
