package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Represents an acknowledgement command
 */
public class CommandAck extends Command
{
	private final String error;

	/**
	 * @param exception If null, this acknowledgement is a success, otherwise a failure with the exception's message
	 */
	public CommandAck(ChatException exception)
	{
		super(exception == null ? Opcode.SUCC : Opcode.ERRO, null);
		error = exception == null ? null : exception.getMessage();
	}

	/**
	 * @return The error message if failure, or null if success
	 */
	public static String readAck(BufferedReader in) throws ChatException
	{
		// read opcode
		Opcode opcode = readOpcode(in);

		switch (opcode)
		{
			case SUCC:
				return null;
			case ERRO:
				return readArgument(in);
			default:
				throw new ChatException("Unexpected ack opcode " + opcode);
		}
	}

	@Override
	public void send(BufferedWriter out) throws ChatException
	{
		sendArgument(out, opcode.serialise());
		if (opcode == Opcode.ERRO)
			sendArgument(out, error);
	}
}
