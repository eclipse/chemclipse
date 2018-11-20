/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.preferences;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.swt.ui.support.Colors;
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
	private Label colorLabel;
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
		//
		createTextElement(compositeMain);
		createColorLabelElement(compositeMain);
		createColorButtonElement(compositeMain);
		//
		return composite;
	}

	private void createTextElement(Composite parent) {

		textName = new Text(parent, SWT.BORDER);
		textName.setText((colorCode != null) ? colorCode.getName() : "");
		textName.setToolTipText("This is the field for the color code name.");
		textName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
	}

	private void createColorLabelElement(Composite parent) {

		colorLabel = new Label(parent, SWT.NONE);
		colorLabel.setText("");
		colorLabel.setToolTipText("This color is used.");
		colorLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		colorLabel.setBackground((colorCode != null) ? colorCode.getColor() : Colors.BLACK);
	}

	private void createColorButtonElement(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Color");
		button.setToolTipText("Select the color.");
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				ColorDialog colorDialog = new ColorDialog(button.getShell());
				colorDialog.setRGB(colorLabel.getBackground().getRGB());
				colorDialog.setText("Select a color.");
				RGB rgb = colorDialog.open();
				if(rgb != null) {
					Color color = Colors.getColor(rgb);
					colorLabel.setBackground(color);
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
		String target = getName();
		if(target.equals("")) {
			setErrorMessage("A name must be specified.");
			getButton(OK).setEnabled(false);
		}
	}

	private String getName() {

		return textName.getText().trim();
	}

	private Color getColor() {

		return colorLabel.getBackground();
	}
}
