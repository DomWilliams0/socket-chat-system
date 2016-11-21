package chatroom;

public class Logger
{
	private Logger()
	{

	}

	public static void error(String message, Object... format)
	{
		System.err.printf("[ERR] " + message + "\n", format);
	}

	public static void log(String message, Object... format)
	{
		System.out.printf("[LOG] " + message + "\n", format);
	}

}
