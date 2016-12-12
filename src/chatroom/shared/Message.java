package chatroom.shared;

import java.io.Serializable;
import java.util.Base64;

/**
 * Represents a message
 */
public class Message implements Serializable
{
	private String from;
	private String content;
	// TODO timestamp

	/**
	 * @param from    The sender of this message
	 * @param content The message's content
	 */
	public Message(String from, String content)
	{
		this.from = from;
		this.content = content;
	}

	/**
	 * @return The message's content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @return The sender of this message
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * Decodes the message content to a readable format
	 */
	public void decode()
	{
		try
		{
			content = new String(Base64.getDecoder().decode(content));
		} catch (IllegalArgumentException e)
		{
			Logger.error("Failed to decode message: %s", e.getMessage());
			content = "<INVALID MESSAGE>";
		}
	}

	/**
	 * Encodes the message for network transmission
	 */
	public void encode()
	{
		content = Base64.getEncoder().encodeToString(content.getBytes());
	}

	/**
	 * @return A formatted string displaying this message
	 */
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
