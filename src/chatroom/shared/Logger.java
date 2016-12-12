package chatroom.shared;

/**
 * Helper class for logging to stdout/stderr
 */
public class Logger
{
	private Logger()
	{
	}

	/**
	 * Logs a formatted message to stderr
	 *
	 * @param message A format string
	 * @param format  Any arguments to substitute into the format string
	 */
	public static void error(String message, Object... format)
	{
		if (message == null)
			return;

		System.out.flush();
		System.err.printf("[ERR] " + message + "\n", format);
		System.err.flush();
	}

	/**
	 * Logs a formatted message to stdout
	 *
	 * @param message A format string
	 * @param format  Any arguments to substitute into the format string
	 */
	public static void log(String message, Object... format)
	{
		if (message == null)
			return;

		System.err.flush();
		System.out.printf("[LOG] " + message + "\n", format);
		System.out.flush();
	}

}
