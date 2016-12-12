package chatroom.client;

import chatroom.shared.protocol.Command;

/**
 * Represents the identity of a chat client
 */
public class ClientIdentity
{
	private final String username;

	/**
	 * @param username The client's username - an {@link IllegalArgumentException} will be thrown if it does not meet
	 *                 certain criteria:
	 *                 it must be longer than 3 characters
	 *                 it must not contain a new line
	 *                 it must not be "SERVER"
	 */
	public ClientIdentity(String username)
	{
		this.username = username;

		if (username == null ||
			username.length() < 3 ||
			username.contains(Command.DELIMITER) ||
			username.equals(Command.SERVER_USERNAME))
			throw new IllegalArgumentException("Invalid username");
	}

	/**
	 * @return The client's username
	 */
	public String getUsername()
	{
		return username;
	}
}
