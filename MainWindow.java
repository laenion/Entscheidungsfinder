/*
 * Created on 25.07.2005
 */
package entscheidungsfinder;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * @author Ignaz Forster
 */
public class MainWindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JScrollPane pane;
	private int x;
	private JPanel inputPanel;
	private JPanel resultPanel;
	private JButton mainButton;
	
	
	MainWindow() {
		//Window settings
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			System.out.println(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		setTitle("Entscheidungsfinder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Window elements
		inputPanel = new JPanel(new BorderLayout());
		JLabel text = new JLabel("<html><font color=\"#ce3c14;\">Bitte geben Sie hier die M&ouml;glichkeiten an, die zur Verf&uuml;gung stehen:</font></html>");
		text.setBorder(new EmptyBorder(5,0,5,0));
		inputPanel.add(text, BorderLayout.NORTH);
		inputPanel.setBorder(new EmptyBorder(3,3,3,3));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		pane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel = new JPanel(new GridBagLayout());
		contentPanel.addComponentListener(new ResizeEvent());
		addNewField();
		validate();
		innerPanel.add(contentPanel);
		innerPanel.add(Box.createVerticalStrut(250));
		innerPanel.add(Box.createHorizontalStrut(20));
		inputPanel.add(pane, BorderLayout.CENTER);
		
		mainButton = new JButton("Was soll ich tun?");
		mainButton.setMnemonic(KeyEvent.VK_W);
		mainButton.addActionListener(this);
		mainButton.setActionCommand("compute");
		mainButton.setBorder(new EmptyBorder(5,3,1,0));
		inputPanel.add(mainButton, BorderLayout.SOUTH);
		
		getContentPane().add(inputPanel);
		getRootPane().setDefaultButton(mainButton);
		pack();
		setVisible(true);
	}
	
	private void addNewField()
	{
		JLabel numberx = new JLabel(contentPanel.getComponentCount()/2+1 + ".");
		JTextField inputx = new JTextField(35);
		inputx.setMaximumSize(inputx.getPreferredSize());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,5);
		c.gridx = 0;
		c.gridy = x++;
		contentPanel.add(numberx, c);
		c.gridx = 1;
		contentPanel.add(inputx, c);
		c.gridy = x+1;
		inputx.addKeyListener(new FieldChange());
		inputx.addFocusListener(new FocusEvents());
		
		addSpacer();
		validate();
	}
	
	private void removeEmptyFields()
	{
		// Only every second field is a JTextField
		for (int i=1; i<contentPanel.getComponentCount()-1; i=i+2)
		{
			JTextField tmp = null;
			if (contentPanel.getComponent(i) instanceof JTextField)
			{
				tmp = (JTextField)contentPanel.getComponent(i);
				if ((tmp.getText().equals("")) && (contentPanel.getComponentCount() > 1))
				{
					contentPanel.remove(contentPanel.getComponent(i-1));
					contentPanel.remove(tmp);
					System.out.println("Component " + i/2 + " removed!");
				}
			}
			refreshNumbers();
		}
		addSpacer();
		validate();
	}
	
	/** This function inserts a spacer at the bottom of contentPanel,
	 * so that the textfields will be on the top of contentPanel 
	 * (instead of being centered vertically). 
	 *
	 * @author ignaz
	 */
	private void addSpacer() {
		int contentHeight = 0;
		int compNum = contentPanel.getComponentCount();
		for (int i=1; i<compNum; i=i+2)
			contentHeight += contentPanel.getComponent(i).getHeight()+4;
		validate();
		if (compNum > 3)
		{
			System.out.println(compNum);
			JLabel lastField = (JLabel)contentPanel.getComponent(compNum-1);
			lastField.setBorder(new EmptyBorder(0,0,contentPanel.getSize().height-contentHeight,0));
		}
	}

	/** This function takes care that the TextFields are always correctly
	 * numbered by overwriting all values.
	 *
	 * @author ignaz
	 */
	private void refreshNumbers()
	{
		for (int i=0; i<contentPanel.getComponentCount()-1; i=i+2)
			if (contentPanel.getComponent(i) instanceof JLabel)
			{
				JLabel tmp2 = (JLabel)contentPanel.getComponent(i);
				tmp2.setText((i+2)/2 + ".");
			}
	}

	/** Executed when a key was pressed in an input field.
	 * If a key was pressed in the last input field of the form,
	 * a new field has be be added to ensure that at least one
	 * field is empty for new entries.
	 * 
	 * @author ignaz
	 */
	class FieldChange extends KeyAdapter
	{
		public void keyReleased(KeyEvent e)
		{
			if (e.getComponent().equals(contentPanel.getComponent(contentPanel.getComponentCount()-2))) {
				addNewField();
			}
		}
	}
	
	/** Executed when one of the input field looses and/or gains it's focus
	 *  Empty fields in the form are deleted then (except the last
	 *  one - handeled in removeEmptyFields().).
	 *  The second method ensures the input field is alway visible
	 *  (in this case in the center of the display area).
	 * 
	 * @author ignaz
	 */
	class FocusEvents extends FocusAdapter {
		public void focusLost(FocusEvent e)
		{
			removeEmptyFields();
		}
		// Scroll to the active field and center it //
		public void focusGained(FocusEvent e)
		{
			pane.getVerticalScrollBar().setValue(e.getComponent().getY()-pane.getHeight()/2);
		}
	}
	
	class ResizeEvent extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			addSpacer();
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof JButton)
		{
			JButton button = (JButton)e.getSource();
			if (e.getActionCommand().equals("compute"))
			{
				resultPanel = new JPanel();
				resultPanel.setLayout(new BorderLayout());
				resultPanel.setBorder(new EmptyBorder(3,3,3,3));
				JLabel label = new JLabel("Das Ergebnis ist:");
				label.setBorder(new EmptyBorder(5,0,5,0));
				JLabel result = new JLabel();
				if (contentPanel.getComponentCount() > 3) {
					JTextField tmp = (JTextField)(contentPanel.getComponent((int)(Math.random() * (contentPanel.getComponentCount()-3))/2*2+1));
					System.out.println("Decision: " + tmp.getText());
					result.setText("<html><font color=\"red\" size=\"+4\">" + tmp.getText() + "</font></html>");
				} else
					result.setText("NICHTS!");
				final JButton okButton = new JButton("OK");
				okButton.setMnemonic(KeyEvent.VK_O);
				okButton.addActionListener(this);
				okButton.setActionCommand("return");
				okButton.setEnabled(false);
				ActionListener okButtonReenable = new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						int i = Integer.parseInt(okButton.getText().substring(4,5));
						okButton.setText("OK (" + i + ")");
						okButton.setEnabled(true);
					}
				};
				new Timer(6000, okButtonReenable).start();  
				
				resultPanel.add(label, BorderLayout.NORTH);
				resultPanel.add(result, BorderLayout.CENTER);
				resultPanel.add(okButton, BorderLayout.SOUTH);
				
				inputPanel.setVisible(false);
				getContentPane().remove(inputPanel);
				super.getContentPane().add(resultPanel);
				super.validate();

				okButton.getRootPane().setDefaultButton(okButton);
				//innerPanel = tmpPanel;
				//button.setEnabled(false);
			}
			if (e.getActionCommand().equals("return"))
			{
				resultPanel.setVisible(false);
				super.remove(resultPanel);
				getContentPane().add(inputPanel);
				inputPanel.setVisible(true);
				super.validate();
				getRootPane().setDefaultButton(mainButton);
			}
		}
	}
}
