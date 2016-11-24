package chatroom.shared.protocol;

import chatroom.shared.ChatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class CommandAck extends Command
{
	private final String error;

	public CommandAck(ChatException exception)
	{
		super(exception == null ? Opcode.SUCC : Opcode.ERRO, null);
		error = exception == null ? null : exception.getMessage();
	}

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
