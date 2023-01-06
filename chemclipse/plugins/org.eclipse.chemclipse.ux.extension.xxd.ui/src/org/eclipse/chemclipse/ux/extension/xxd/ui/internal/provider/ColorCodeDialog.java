/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCode;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCodes;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ColorCodeDialog extends TitleAreaDialog {

	private static final Logger logger = Logger.getLogger(ColorCodeDialog.class);
	//
	private Text textName;
	private Label colorWidget;
	//
	private ColorCode colorCode;

	public ColorCodeDialog(Shell shell) {

		this(shell, null);
	}

	public ColorCodeDialog(Shell shell, ColorCode colorCodePCR) {

		super(shell);
		this.colorCode = colorCodePCR;
	}

	public ColorCode getColorCode() {

		return colorCode;
	}

	@Override
	protected Control createContents(Composite parent) {

		Control contents = super.createContents(parent);
		setTitle("Color Code");
		validate();
		return contents;
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {

		if(buttonId == IDialogConstants.OK_ID) {
			try {
				colorCode = new ColorCode(getName(), getColor());
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		super.buttonPressed(buttonId);
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		//
		Composite compositeMain = new Composite(composite, SWT.FILL);
		compositeMain.setLayout(new GridLayout(3, false));
		GridData gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 550;
		composite.setLayoutData(gridData);
		//
		createTextElement(compositeMain);
		createColorDisplayElement(compositeMain);
		createColorButtonElement(compositeMain);
		//
		return composite;
	}

	private void createTextElement(Composite parent) {

		textName = new Text(parent, SWT.BORDER);
		textName.setText((colorCode != null) ? colorCode.getName() : "");
		textName.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.FIELD_FOR_COLOR_CODE_NAME));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 300;
		textName.setLayoutData(gridData);
		textName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
	}

	private void createColorDisplayElement(Composite parent) {

		colorWidget = new Label(parent, SWT.BORDER);
		colorWidget.setText("");
		colorWidget.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.COLOR_IS_USED));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 150;
		colorWidget.setLayoutData(gridData);
		colorWidget.setBackground((colorCode != null) ? colorCode.getColor() : Colors.BLACK);
	}

	private void createColorButtonElement(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Color");
		button.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SELECT_COLOR));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				ColorDialog colorDialog = new ColorDialog(event.display.getActiveShell());
				colorDialog.setRGB(colorWidget.getBackground().getRGB());
				colorDialog.setText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SELECT_COLOR));
				RGB rgb = colorDialog.open();
				if(rgb != null) {
					Color color = Colors.getColor(rgb);
					colorWidget.setBackground(color);
				}
			}
		});
	}

	@Override
	protected Control createButtonBar(Composite parent) {

		return super.createButtonBar(parent);
	}

	private void validate() {

		setErrorMessage(null);
		getButton(OK).setEnabled(true);
		//
		String name = getName();
		if(name.equals("")) {
			setErrorMessage(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.NAME_MUST_BE_SPECIFIED));
			getButton(OK).setEnabled(false);
		} else {
			String NameMustNotContain = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.NAME_MUST_NOT_CONTAIN);
			if(name.contains(ColorCodes.VALUE_DELIMITER)) {
				setErrorMessage(NameMustNotContain + " '" + ColorCodes.VALUE_DELIMITER + "'");
				getButton(OK).setEnabled(false);
			} else if(name.contains(ColorCodes.ENTRY_DELIMITER)) {
				setErrorMessage(NameMustNotContain + " '" + ColorCodes.ENTRY_DELIMITER + "'");
				getButton(OK).setEnabled(false);
			}
		}
	}

	private String getName() {

		return textName.getText().trim();
	}

	private Color getColor() {

		return colorWidget.getBackground();
	}
}
