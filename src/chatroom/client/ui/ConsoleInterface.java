package chatroom.client.ui;

import chatroom.client.ClientConnection;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleInterface implements IInterface
{
	private final PrintStream stream;

	public ConsoleInterface(PrintStream stream)
	{
		this.stream = stream;
	}

	@Override
	public void display(String message)
	{
		stream.flush();
		stream.print(message);
		stream.print("\n");
	}

	@Override
	public void start(ClientConnection clientConnection)
	{
		Scanner scanner = new Scanner(System.in);
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
