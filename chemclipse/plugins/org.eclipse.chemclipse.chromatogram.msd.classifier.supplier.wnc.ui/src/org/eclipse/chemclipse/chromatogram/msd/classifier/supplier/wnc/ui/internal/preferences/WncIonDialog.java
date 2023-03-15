/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.preferences;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.l10n.Messages;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class WncIonDialog extends TitleAreaDialog {

	private static final Logger logger = Logger.getLogger(WncIonDialog.class);
	private String title;
	private String message;
	private Text textName;
	private Text textIon;
	private IWncIon wncIon;

	public WncIonDialog(Shell shell) {

		this(shell, null);
	}

	public WncIonDialog(Shell shell, IWncIon wncIon) {

		super(shell);
		this.title = Messages.wncIon;
		this.message = Messages.addIonToAnalyze;
		this.wncIon = wncIon;
	}

	/**
	 * This method could return null.
	 * 
	 * @return IWNCIon
	 */
	public IWncIon getWNCIon() {

		return wncIon;
	}

	@Override
	protected Control createContents(Composite parent) {

		Control contents = super.createContents(parent);
		setTitle(title);
		setMessage(message, IMessageProvider.INFORMATION);
		return contents;
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
			 * If the validation is OK, than create the object.
			 */
			try {
				int ion = getIon();
				String name = getName();
				wncIon = new WncIon(ion, name);
			} catch(Exception e) {
				logger.warn(e);
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
		/*
		 * Layout Data
		 */
		Composite elementComposite = new Composite(composite, SWT.FILL);
		elementComposite.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 400;
		elementComposite.setLayoutData(gridData);
		/*
		 * Name
		 */
		Label labelName = new Label(elementComposite, SWT.NONE);
		labelName.setLayoutData(gridData);
		labelName.setText(NLS.bind(Messages.nameWithoutDisallowedCharacters, PreferenceSupplier.VALUE_DELIMITER, PreferenceSupplier.ENTRY_DELIMITER));
		/*
		 * Name Input
		 */
		GridData gridDataText = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridDataText.widthHint = 300;
		textName = new Text(elementComposite, SWT.BORDER);
		textName.setLayoutData(gridDataText);
		if(wncIon != null) {
			textName.setText(wncIon.getName());
		}
		textName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
		/*
		 * ion
		 */
		Label labelIon = new Label(elementComposite, SWT.NONE);
		labelIon.setText(Messages.ion);
		/*
		 * ion Input
		 */
		textIon = new Text(elementComposite, SWT.BORDER);
		if(wncIon != null) {
			textIon.setText(Integer.toString(wncIon.getIon()));
		}
		textIon.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
		return composite;
	}

	private void validate() {

		setErrorMessage(null);
		getButton(OK).setEnabled(true);
		/*
		 * Name
		 */
		String name = getName();
		if(name.isEmpty()) {
			setErrorMessage(Messages.nameMustBeSpecified);
			getButton(OK).setEnabled(false);
		}
		/*
		 * Ion
		 */
		try {
			getIon();
		} catch(NumberFormatException e) {
			setErrorMessage(Messages.ionValueMustBeInteger);
			getButton(OK).setEnabled(false);
		}
	}

	private int getIon() throws NumberFormatException {

		return Integer.valueOf(textIon.getText());
	}

	private String getName() {

		return textName.getText().trim();
	}
}
