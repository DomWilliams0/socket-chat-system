package chatroom.client.ui;

import chatroom.client.ClientConnection;
import chatroom.shared.Message;

/**
 * Interface that represents a user interface
 */
public interface IInterface
{
	/**
	 * Starts the UI - this should block until the UI is closed
	 *
	 * @param clientConnection The connection to the server
	 */
	void start(ClientConnection clientConnection);

	/**
	 * Displays the given string on the UI
	 *
	 * @param message The string to display
	 */
	void display(String message);

	/**
	 * Displays the given message on the UI with the default message formatting
	 *
	 * @param message The message to display
	 */
	default void displayMessage(Message message)
	{
		display(message.format());
	}


	// TODO user list
}
