/*
Copyright (c) 2014, Ignaz Forster
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.util.Random;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;

public class EntscheidungsfinderME extends MIDlet implements CommandListener {
	Display display;
	Form form;
	Command exit;
	Command decision;
	Item lastFocusedItem;
	int inputItems = 0;

	public void startApp() {
		display = Display.getDisplay(this);
		form = new Form(LocalizationSupport.getMessage("PROGRAM_TITLE"));
		form.append(LocalizationSupport.getMessage("ENTER_OPTIONS"));
		form.setItemStateListener(new TextFieldListener());
		form.setCommandListener(this);
		exit = new Command(LocalizationSupport.getMessage("EXIT"), Command.EXIT, 1);
		form.addCommand(exit);
		decision = new Command(LocalizationSupport.getMessage("BUTTON_CALCULATE_SHORT"), LocalizationSupport.getMessage("BUTTON_CALCULATE"), Command.SCREEN, 2);
		form.addCommand(decision);

		lastFocusedItem = addField();
		addField();
		addField();
		display.setCurrent(form);
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
	}

	protected Item addField() {
		inputItems++;
		TextField input = new TextField(inputItems + ".", null, 100, TextField.ANY);
		input.setLayout(TextField.LAYOUT_EXPAND | TextField.LAYOUT_SHRINK);
		form.append(input);
		return input;
	}

	protected void changeLabel(TextField textfield, int counter) {
		if (!textfield.getLabel().equals(counter + ".")) {
			textfield.setLabel(counter + ".");
		}
	}

	/**
	 * Parse the list, remove empty fields and fix numeration.
	 * 
	 * As it's not sure when itemStateChanged will be called - it may be
	 * either after typing a single key or after leaving a TextField -
	 * be sure always keep 2 spare empty fields so that the user
	 * has something to change to in the later case.
	 *
	 * @param item
	 */
	protected void fixList(Item item) {
		TextField textfield;
		for (int i = 1; i <= inputItems; i++) {
			textfield = (TextField) form.get(i);
			// Check for empty fields, but leave the last two fields alone
			if (textfield.getString().equals("")) {
				if (i <= inputItems - 2) {
					deleteField(i);
					i--;
				}
			} else { // Make sure the last two field are empty
				if (i >= inputItems - 1) {
					addField();
					fixFocus(i);

					// Someone entered something in the last field - go back and clean up...
					if (i == inputItems - 1) {
						i = i - 2;
					}
				}
			}
			changeLabel(textfield, i);
		}
	}

	/**
	 * Focus handling seems to be broken in the Sun Java Wireless Toolkit Emulator when adding new elements.
	 * Make sure that the user will continue typing in the field where he started
	 * @param i Field to set the focus on
	 */
	protected void fixFocus(int i) {
		display.setCurrentItem(form.get(i - 1));
		display.setCurrentItem(form.get(i));
	}

	protected void deleteField(int i) {
		form.delete(i);
		display.setCurrentItem(form.get(i));
		inputItems--;
	}

	public void commandAction(Command c, Displayable s) {
		if (c == exit) {
			destroyApp(false);
			notifyDestroyed();
		}

		if (c == decision) {
			String result;
			
			// Clean up fields in case phone implementation didn't call ItemStateListener yet
			fixList(form.get(1));

			if (((TextField)form.get(1)).getString().equals("")) {
				result = LocalizationSupport.getMessage("NOTHING");
			} else {
				Random randomizer = new Random();
				result = ((TextField) form.get(randomizer.nextInt(inputItems - 2) + 1)).getString();
			}
			Alert resultScreen = new Alert(LocalizationSupport.getMessage("RESULT"), result, null, AlertType.INFO);
			resultScreen.setTimeout(Alert.FOREVER);
			display.setCurrent(resultScreen, form);
		}
	}

	protected class TextFieldListener implements ItemStateListener {
		public void itemStateChanged(Item item) {
			// Simulte focusLost
			if (lastFocusedItem == item) {
				return;
			}
			fixList(item);

			lastFocusedItem = item;
		}
	}
}
