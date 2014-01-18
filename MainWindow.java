/*
Copyright (c) 2005-2011, Ignaz Forster
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Created on 25.07.2005
 */
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
		super();
		setTitle("Entscheidungsfinder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(450, 350));

		//Window elements
		inputPanel = new JPanel(new BorderLayout());
		JLabel text = new JLabel("<html><font color=\"#ce3c14;\">Bitte geben Sie hier die M&ouml;glichkeiten an, die zur Verf&uuml;gung stehen:</font></html>");
		text.setBorder(new EmptyBorder(5, 0, 5, 0));
		inputPanel.add(text, BorderLayout.NORTH);
		inputPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		pane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPanel = new JPanel();
		GridBagLayout gridbaglayout = new GridBagLayout();
		gridbaglayout.rowHeights = new int[]{10, 0, 0};
		gridbaglayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridbaglayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gridbaglayout);
		addNewField();
		validate();
		innerPanel.add(contentPanel);
		inputPanel.add(pane, BorderLayout.CENTER);

		mainButton = new JButton("Was soll ich tun?");
		mainButton.setMnemonic(KeyEvent.VK_W);
		mainButton.addActionListener(this);
		mainButton.setActionCommand("compute");
		mainButton.setBorder(new EmptyBorder(5, 3, 1, 0));
		inputPanel.add(mainButton, BorderLayout.SOUTH);

		getContentPane().add(inputPanel);
		getRootPane().setDefaultButton(mainButton);
		pack();
		setVisible(true);
	}

	private void addNewField() {
		// Add numbering
		JLabel numberx = new JLabel(contentPanel.getComponentCount() / 2 + 1 + ".");
		JTextField inputx = new JTextField(20);
		GridBagConstraints numberGridPos = new GridBagConstraints();
		numberGridPos.insets = new Insets(2, 2, 2, 5);
		numberGridPos.anchor = GridBagConstraints.NORTH;
		numberGridPos.gridx = 0;
		numberGridPos.gridy = contentPanel.getComponentCount() / 2;
		//numberGridPos.weightx = 0;
		//numberGridPos.weighty = 0;
		contentPanel.add(numberx, numberGridPos);
		
		// Add input field
		GridBagConstraints inputfieldGridPos = new GridBagConstraints();
		inputfieldGridPos.fill = GridBagConstraints.HORIZONTAL;
		inputfieldGridPos.anchor = GridBagConstraints.NORTH;
		inputfieldGridPos.gridx = 1;
		inputfieldGridPos.gridy = contentPanel.getComponentCount() / 2;
		contentPanel.add(inputx, inputfieldGridPos);
		inputx.addKeyListener(new FieldChange());
		inputx.addFocusListener(new FocusEvents());

		validate();
	}

	private void removeEmptyFields() {
		// Only every second field is a JTextField
		for (int i = 1; i < contentPanel.getComponentCount() - 1; i = i + 2) {
			JTextField tmp = null;
			if (contentPanel.getComponent(i) instanceof JTextField) {
				tmp = (JTextField) contentPanel.getComponent(i);
				if ((tmp.getText().equals("")) && (contentPanel.getComponentCount() > 1)) {
					contentPanel.remove(contentPanel.getComponent(i - 1));
					contentPanel.remove(tmp);
					System.out.println("Component " + i / 2 + " removed!");
				}
			}
			refreshNumbers();
		}
		validate();
	}

	/** This function takes care that the TextFields are always correctly
	 * numbered by overwriting all values.
	 */
	private void refreshNumbers() {
		for (int i = 0; i < contentPanel.getComponentCount(); i = i + 2) {
			if (contentPanel.getComponent(i) instanceof JLabel) {
				JLabel tmp2 = (JLabel) contentPanel.getComponent(i);
				tmp2.setText((i + 2) / 2 + ".");
			}
		}
	}

	/** Executed when a key was pressed in an input field.
	 * If a key was pressed in the last input field of the form,
	 * a new field has be be added to ensure that at least one
	 * field is empty for new entries.
	 */
	class FieldChange extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			if (e.getComponent().equals(contentPanel.getComponent(contentPanel.getComponentCount() - 1))) {
				addNewField();
			}
		}
	}

	/** Executed when one of the input field looses and/or gains it's focus
	 *  Empty fields in the form are deleted then (except the last
	 *  one - handeled in removeEmptyFields().).
	 *  The second method ensures the input field is alway visible
	 *  (in this case in the center of the display area).
	 */
	class FocusEvents extends FocusAdapter {

		public void focusLost(FocusEvent e) {
			removeEmptyFields();
		}
		// Scroll to the active field and center it //

		public void focusGained(FocusEvent e) {
			pane.getVerticalScrollBar().setValue(e.getComponent().getY() - pane.getHeight() / 2);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton button = (JButton) e.getSource();
			if (e.getActionCommand().equals("compute")) {
				resultPanel = new JPanel();
				resultPanel.setLayout(new BorderLayout());
				resultPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
				JLabel label = new JLabel("Das Ergebnis ist:");
				label.setBorder(new EmptyBorder(5, 0, 5, 0));
				JLabel result = new JLabel();
				if (contentPanel.getComponentCount() > 2) {
					JTextField tmp = (JTextField) (contentPanel.getComponent((int) (Math.random() * (contentPanel.getComponentCount() - 2)) / 2 * 2 + 1));
					System.out.println("Decision: " + tmp.getText());
					result.setText("<html><font color=\"red\" size=\"+4\">" + tmp.getText() + "</font></html>");
				} else {
					result.setText("NICHTS!");
				}
				JButton okButton = new JButton("OK");
				okButton.setMnemonic(KeyEvent.VK_O);
				okButton.addActionListener(this);
				okButton.setActionCommand("return");

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
			if (e.getActionCommand().equals("return")) {
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
