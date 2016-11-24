package chatroom.shared;

import java.io.IOException;

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

	public void printStackTrace()
	{
		Logger.error(getMessage());
	}

	public boolean isSerious()
	{
		return serious;
	}
}
