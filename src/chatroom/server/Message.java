package chatroom.server;

import chatroom.Protocol;

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
		content = Protocol.decodeMessage(content);
	}


}
