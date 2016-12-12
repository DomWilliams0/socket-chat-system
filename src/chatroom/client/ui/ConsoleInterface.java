package chatroom.client.ui;

import chatroom.client.ClientConnection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleInterface implements IInterface
{
	private final PrintStream out;
	private final InputStream in;

	public ConsoleInterface(PrintStream out, InputStream in)
	{
		this.out = out;
		this.in = in;
	}

	@Override
	public void display(String message)
	{
		out.flush();
		out.print(message);
		out.print("\n");
	}

	@Override
	public void start(ClientConnection clientConnection)
	{
		Scanner scanner = new Scanner(in);
		System.out.println("Enter a message to send, or /quit to leave");
		while (true)
		{
			String line = scanner.nextLine();
			if (line.equals("/quit"))
				break;

			if (line.isEmpty())
				continue;

			clientConnection.sendMessage(line);
		}

		scanner.close();
	}
}
