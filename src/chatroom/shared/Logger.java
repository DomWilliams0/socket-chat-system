package chatroom.shared;

public class Logger
{
	private Logger()
	{

	}

	public static void error(String message, Object... format)
	{
		if (message == null)
			return;

		System.out.flush();
		System.err.printf("[ERR] " + message + "\n", format);
		System.err.flush();
	}

	public static void log(String message, Object... format)
	{
		if (message == null)
			return;

		System.err.flush();
		System.out.printf("[LOG] " + message + "\n", format);
		System.out.flush();
	}

}
