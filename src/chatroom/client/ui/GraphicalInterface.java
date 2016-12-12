package chatroom.client.ui;

import chatroom.client.ClientConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Represents a simple graphical UI
 */
public class GraphicalInterface implements IInterface
{
	private final java.util.List<String> buffer;
	private JDialog frame;
	private JTextArea history;

	public GraphicalInterface()
	{
		buffer = new ArrayList<>();
	}

	@Override
	public void start(ClientConnection clientConnection)
	{
		try
		{
			SwingUtilities.invokeAndWait(() ->
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
				{
					// no prettiness for you
				}


				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

				JPanel controlPanel = new JPanel();
				JTextField chatBox = new JTextField(12);
				JButton buttonSend = new JButton("Send");
				JButton buttonQuit = new JButton("Quit");

				controlPanel.add(chatBox);
				controlPanel.add(buttonSend);
				controlPanel.add(buttonQuit);

				JPanel historyPanel = new JPanel(new BorderLayout());
				int historyPad = 10;
				history = new JTextArea(12, 12);
				history.setEditable(false);
				JScrollPane historyScroll = new JScrollPane(history);
				historyPanel.add(historyScroll, BorderLayout.CENTER);
				historyPanel.setBorder(BorderFactory.createEmptyBorder(historyPad, historyPad, 0, historyPad));

				panel.add(historyPanel);
				panel.add(controlPanel);

				chatBox.addKeyListener(new KeyAdapter()
				{
					@Override
					public void keyPressed(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_ENTER)
							buttonSend.doClick();

					}
				});
				buttonSend.addActionListener(new AbstractAction()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						clientConnection.sendMessage(chatBox.getText());
						SwingUtilities.invokeLater(() -> chatBox.setText(""));
					}
				});

				buttonQuit.addActionListener(new AbstractAction()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						clientConnection.disconnect();
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				});

				// add queued messages in buffer
				buffer.forEach(this::reallyDisplayMessage);
				buffer.clear();

				frame = new JDialog();
				frame.setModal(true);
				frame.setContentPane(panel);
				frame.pack();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			});
		} catch (InterruptedException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Displays the given string if the UI has been fully initialised, otherwise adds it to a buffer
	 */
	@Override
	public void display(String message)
	{
		if (history == null)
			buffer.add(message);
		else
			reallyDisplayMessage(message);
	}

	/**
	 * Actually displays the given string on the UI
	 */
	private void reallyDisplayMessage(String message)
	{
		SwingUtilities.invokeLater(() ->
			history.append(message + "\n")
		);
	}
}
