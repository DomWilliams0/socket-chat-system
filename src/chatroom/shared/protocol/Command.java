package chatroom.shared.protocol;


import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Abstract class representing an instance of a command
 */
public abstract class Command
{
	public static final String DELIMITER = "\n";
	public static final String SERVER_USERNAME = "SERVER";

	protected final Opcode opcode;
	protected final String username;

	/**
	 * @param opcode   The command opcode
	 * @param username The sender's username
	 */
	protected Command(Opcode opcode, String username)
	{
		this.opcode = opcode;
		this.username = username;
	}

	/**
	 * Writes the given argument, followed by a delimiter
	 *
	 * @param out The writer to write to
	 * @param arg The argument to write
	 */
	static void sendArgument(BufferedWriter out, String arg) throws ChatException
	{
		try
		{
			out.write(arg);
			out.write(DELIMITER);
			out.flush();
		} catch (IOException e)
		{
			throw new ChatException(e);
		}
	}

	/**
	 * Reads an argument from the given reader
	 *
	 * @param in The reader to read from
	 * @return An argument
	 */
	protected static String readArgument(BufferedReader in) throws ChatException
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

	/**
	 * Reads and parses an opcode from the given reader
	 *
	 * @param in The reader to read from
	 * @return The parsed opcode
	 */
	protected static Opcode readOpcode(BufferedReader in) throws ChatException
	{
		String opcodeStr = readArgument(in);

		Opcode opcode = Opcode.parse(opcodeStr);
		if (opcode == null)
			throw new ChatException("Invalid opcode '" + opcodeStr + "'");

		return opcode;
	}


	/**
	 * Reads and parses a full command prologue from the given reader
	 *
	 * @param in              The reader to read from
	 * @param expectedOpcodes A subset of opcodes to allow
	 * @return The parsed prologue
	 * @throws IllegalArgumentException If the parsed opcode is not in the provided subset of expected opcodes
	 */
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

	/**
	 * Write this command to the given writer
	 *
	 * @param out The writer
	 */
	public void send(BufferedWriter out) throws ChatException
	{
		sendPrologue(out); // no extra args
	}

	/**
	 * Reads from the given reader
	 *
	 * @param in The reader
	 */
	public String read(BufferedReader in) throws ChatException
	{
		return readArgument(in);
	}

	/**
	 * Send a full prologue to the given writer
	 *
	 * @param out The writer
	 */
	private void sendPrologue(BufferedWriter out) throws ChatException
	{
		// start with opcode
		sendArgument(out, opcode.serialise());

		// followed by username
		sendArgument(out, username);

		// followed by any opcode specific arguments
	}

	/**
	 * @return The command opcode
	 */
	public Opcode getOpcode()
	{
		return opcode;
	}
}
