package chatroom.client.ui;

import chatroom.client.ClientConnection;
import chatroom.server.Message;

public interface IInterface
{
	/**
	 * Should block
	 */
	void start(ClientConnection clientConnection);

	void display(String message);

	default void displayMessage(Message message)
	{
		display(message.format());
	}


	// TODO user list
}
