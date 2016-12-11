package chatroom.shared;

import chatroom.shared.protocol.Protocol;

import java.io.Serializable;

public class Message implements Serializable
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

	public void encode()
	{
		content = Protocol.encodeMessage(content);
	}

	public String format()
	{
		return String.format("[%s] %s", from, content);
	}

	@Override
	public String toString()
	{
		return "Message{" +
			"from='" + from + '\'' +
			", content='" + content + '\'' +
			'}';
	}
}
