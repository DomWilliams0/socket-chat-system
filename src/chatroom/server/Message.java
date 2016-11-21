package chatroom.server;

import chatroom.Logger;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

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

	public String decode()
	{
		try
		{
			return new String(Base64.decode(content));
		} catch (Base64DecodingException e)
		{
			Logger.error("Failed to decode message: %s", e.getMessage());
			return "<INVALID MESSAGE>";
		}
	}
}
