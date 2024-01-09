/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.dialogs;

import java.io.IOException;

import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PerspectiveChooserDialog extends Dialog {

	private String title = "";
	private String message = "";
	private Label messageLabel;
	private Button changePerspectiveAutomatically;
	private IPreferenceStore preferenceStore;
	private String preferenceKey;

	public PerspectiveChooserDialog(Shell parentShell, String title, String message) {

		this(parentShell, title, message, Activator.getDefault().getPreferenceStore(), PreferenceSupplier.P_CHANGE_PERSPECTIVE_AUTOMATICALLY);
	}

	public PerspectiveChooserDialog(Shell parentShell, String title, String message, IPreferenceStore preferenceStore, String preferenceKey) {

		super(parentShell);
		this.preferenceStore = preferenceStore;
		this.preferenceKey = preferenceKey;
		if(title != null) {
			this.title = title;
		}
		if(message != null) {
			this.message = message;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		if(title != null) {
			shell.setText(title);
		}
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	@Override
	protected void buttonPressed(int buttonId) {

		/*
		 * Set values only if the OK button has been clicked.
		 */
		if(buttonId == IDialogConstants.OK_ID) {
			/*
			 * Check if the user would like to see the message again.
			 */
			preferenceStore.setValue(preferenceKey, changePerspectiveAutomatically.getSelection());
			if(preferenceStore instanceof IPersistentPreferenceStore store) {
				if(store.needsSaving()) {
					try {
						store.save();
					} catch(IOException e) {
						Activator.getDefault().getLog().log(new Status(IStatus.ERROR, getClass().getName(), "Saving preferences failed", e));
					}
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		createMessage(composite);
		createCheckboxes(composite, layoutData);
		return composite;
	}

	/**
	 * Sets the dialog message.
	 */
	private void createMessage(Composite parent) {

		if(message != null) {
			GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			layoutData.minimumHeight = 100;
			messageLabel = new Label(parent, SWT.WRAP);
			messageLabel.setLayoutData(layoutData);
			messageLabel.setText(message);
			messageLabel.setFont(parent.getFont());
		}
	}

	/**
	 * Creates the checkboxes.
	 * 
	 * @param parent
	 * @param layoutData
	 */
	private void createCheckboxes(Composite parent, GridData layoutData) {

		changePerspectiveAutomatically = new Button(parent, SWT.CHECK);
		changePerspectiveAutomatically.setText("Change perspectives automatically and don't ask again");
		changePerspectiveAutomatically.setLayoutData(layoutData);
		changePerspectiveAutomatically.setSelection(preferenceStore.getBoolean(preferenceKey));
	}
}
