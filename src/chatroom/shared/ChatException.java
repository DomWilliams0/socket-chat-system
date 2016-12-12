package chatroom.shared;

import java.io.IOException;

/**
 * General chatroom related exception
 */
public class ChatException extends Exception
{
	private boolean serious = false;

	public ChatException()
	{
	}

	public ChatException(String message)
	{
		super(message);
	}

	public ChatException(Throwable cause)
	{
		this(cause.getMessage());
		if (cause instanceof IOException)
			serious = true;
	}

	@Override
	public void printStackTrace()
	{
		Logger.error(getMessage());
	}

	/**
	 * @return If this exception is fatal (e.g. an {@link IOException}
	 */
	public boolean isSerious()
	{
		return serious;
	}
}
