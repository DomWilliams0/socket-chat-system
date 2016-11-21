package chatroom.server;

import chatroom.Logger;

public class Message
{
	private String from;
	private String content;
	// TODO timestamp


	public Message(String from, String content)
	{
		this.from = from;
		this.content = content;
	}

	public String getContent()
	{
		return content;
	}

	public String getFrom()
	{
		return from;
	}

	public void decode()
	{
		try
		{
			content = new String(java.util.Base64.getDecoder().decode(content));
		} catch (IllegalArgumentException e)
		{
			Logger.error("Failed to decode message: %s", e.getMessage());
			content = "<INVALID MESSAGE>";
		}
	}


}
