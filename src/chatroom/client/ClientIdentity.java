package chatroom.client;

import chatroom.shared.protocol.Protocol;

public class ClientIdentity
{
	private final String username;

	public ClientIdentity(String username)
	{
		this.username = username;

		if (username == null ||
			username.length() < 3 ||
			username.contains(Protocol.DELIMITER) ||
			username.equals(Protocol.SERVER_USERNAME))
			throw new IllegalArgumentException("Invalid username");
	}

	public String getUsername()
	{
		return username;
	}
}
