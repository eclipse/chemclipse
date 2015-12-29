/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.ui.runnables.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.chemclipse.database.model.entries.SubstanceEntry;

public class SubstanceNameDialog extends Dialog {

	private String title;
	private String message;
	private Label messageLabel;
	private Combo substanceCombo;
	private SubstanceEntry substanceEntry;
	private String[] availableSubstances;

	public SubstanceNameDialog(Shell shell, SubstanceEntry substanceEntry, String[] availableSubstances, String title) {
		super(shell);
		this.title = title;
		this.message = "Selected substance name.";
		this.substanceEntry = substanceEntry;
		if(availableSubstances != null) {
			this.availableSubstances = availableSubstances;
		} else {
			this.availableSubstances = new String[]{""};
		}
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

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {

		/*
		 * Set values only if the OK button has been clicked.
		 */
		if(buttonId == IDialogConstants.OK_ID) {
			substanceEntry.setSubstanceName(substanceCombo.getText().trim());
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected Control createButtonBar(Composite parent) {

		Control control = super.createButtonBar(parent);
		return control;
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		createMessage(composite, layoutData);
		createSubstanceNameComboBox(composite, layoutData);
		validateSubstanceName();
		return composite;
	}

	/**
	 * Sets the dialog message.
	 */
	private void createMessage(Composite parent, GridData layoutData) {

		if(message != null) {
			messageLabel = new Label(parent, SWT.WRAP);
			messageLabel.setText(message);
			messageLabel.setLayoutData(layoutData);
			messageLabel.setFont(parent.getFont());
		}
	}

	private void createSubstanceNameComboBox(Composite parent, GridData layoutData) {

		substanceCombo = new Combo(parent, SWT.NONE);
		substanceCombo.setLayoutData(layoutData);
		substanceCombo.setItems(availableSubstances);
		/*
		 * Select a substance name.
		 */
		substanceCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				validateSubstanceName();
			}
		});
		/*
		 * Type in a new name.
		 */
		substanceCombo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validateSubstanceName();
			}
		});
	}

	private void validateSubstanceName() {

		String substance = substanceCombo.getText().trim();
		if(substance == null || substance.equals("")) {
			/*
			 * This is a valid trial version. The ok button must be enabled.
			 */
			setErrorMessage("The substance must have a name.");
		} else {
			setErrorMessage("");
		}
	}

	private void setErrorMessage(String errorMessage) {

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
