/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractEntryEditDialog extends Dialog {

	private static final int NUMBER_OF_COLUMNS = 3;
	private String title;
	private String message;
	private Label messageLabel;
	private Map<String, String> widgetInput;

	public AbstractEntryEditDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
		widgetInput = new HashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		if(title != null) {
			shell.setText(title);
		}
	}

	@Override
	protected Control createButtonBar(Composite parent) {

		Control control = super.createButtonBar(parent);
		setErrorMessage("Please fill in/edit the fields.");
		return control;
	}

	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = NUMBER_OF_COLUMNS;
		composite.setLayout(gridLayout);
		createMessage(composite);
		//
		return composite;
	}

	protected String getWidgetInput(String key) {

		return widgetInput.get(key);
	}

	/**
	 * Sets the dialog message.
	 */
	private void createMessage(Composite parent) {

		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		layoutData.horizontalSpan = NUMBER_OF_COLUMNS;
		layoutData.heightHint = 50;
		if(message != null) {
			messageLabel = new Label(parent, SWT.WRAP);
			messageLabel.setText(message);
			messageLabel.setLayoutData(layoutData);
			messageLabel.setFont(parent.getFont());
			messageLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	protected void createTextInput(Composite parent, GridData layoutData, final String labelText, String labelUnit, String textDefault, boolean editable) {

		createLabel(parent, labelText);
		//
		final Text text = new Text(parent, SWT.BORDER);
		text.setText(textDefault);
		text.setLayoutData(layoutData);
		widgetInput.put(labelText, text.getText());
		text.setEditable(editable);
		text.setEnabled(editable);
		/*
		 * Don't type in a new database name.
		 */
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				widgetInput.put(labelText, text.getText());
				validateInput();
			}
		});
		/*
		 * Unit
		 */
		createLabel(parent, labelUnit);
	}

	protected void createCheckInput(Composite parent, GridData layoutData, final String labelText, String labelUnit, boolean selectionDefault) {

		createLabel(parent, labelText);
		//
		final Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(layoutData);
		button.setSelection(selectionDefault);
		widgetInput.put(labelText, Boolean.toString(selectionDefault));
		/*
		 * Don't type in a new database name.
		 */
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				widgetInput.put(labelText, Boolean.toString(button.getSelection()));
				validateInput();
			}
		});
		/*
		 * Unit
		 */
		createLabel(parent, labelUnit);
	}

	protected void createEnumInput(Composite parent, GridData layoutData, final String labelText, String labelUnit, String[] items, String textDefault) {

		createLabel(parent, labelText);
		//
		final Combo combo = new Combo(parent, SWT.NONE);
		combo.setItems(items);
		combo.setLayoutData(layoutData);
		combo.setText(textDefault);
		widgetInput.put(labelText, combo.getText());
		/*
		 * Don't type in a new database name.
		 */
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				widgetInput.put(labelText, combo.getText());
				validateInput();
			}
		});
		/*
		 * Unit
		 */
		createLabel(parent, labelUnit);
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}

	/**
	 * Override the method, if additional tests are
	 * neccessary.
	 * 
	 * @return boolean.
	 */
	protected boolean validateInput() {

		boolean valid = false;
		exitloop:
		for(Map.Entry<String, String> entry : widgetInput.entrySet()) {
			String text = entry.getValue();
			if(text == null || text.equals("")) {
				setErrorMessage(entry.getKey() + " must be not null or empty.");
				break exitloop;
			} else {
				valid = true;
			}
		}
		//
		if(valid) {
			/*
			 * Default no error message
			 */
			setErrorMessage("");
		}
		return valid;
	}

	protected void setErrorMessage(String errorMessage) {

		Control button = getButton(IDialogConstants.OK_ID);
		/*
		 * Show the product error message if available.
		 */
		if(errorMessage != null && !errorMessage.equals("")) {
			/*
			 * Set the error message.
			 */
			messageLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			messageLabel.setText(errorMessage);
			/*
			 * There is an error. Disable the ok button.
			 */
			if(button != null) {
				button.setEnabled(false);
			}
		} else {
			/*
			 * Remove the error message.
			 */
			if(messageLabel != null && !messageLabel.isDisposed()) {
				messageLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				messageLabel.setText(message);
			}
			/*
			 * If there is no error, the input must be valid.
			 */
			if(button != null) {
				button.setEnabled(true);
			}
		}
	}
}
