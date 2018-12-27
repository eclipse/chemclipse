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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables.dialogs;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller.QuantitationSignalEntryEdit;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class QuantitationSignalEntryEditDialog extends AbstractEntryEditDialog {

	private static final Logger logger = Logger.getLogger(QuantitationSignalEntryEditDialog.class);
	//
	private static final String KEY_ION = "Ion";
	private static final String KEY_RELATIVE_RESPONSE = "Relative Response";
	private static final String KEY_UNCERTAINTY = "Uncertainty";
	private static final String KEY_USE = "Use";
	//
	private QuantitationSignalEntryEdit quantitationSignalEntryEdit;
	//
	private DecimalFormat decimalFormat;

	public QuantitationSignalEntryEditDialog(Shell parentShell, QuantitationSignalEntryEdit quantitationSignalEntryEdit, String title) {
		super(parentShell, title, "Create/Edit a quantitation signal entry.");
		this.quantitationSignalEntryEdit = quantitationSignalEntryEdit;
		//
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {

		/*
		 * Set values only if the OK button has been clicked.
		 */
		if(buttonId == IDialogConstants.OK_ID) {
			try {
				double ion = decimalFormat.parse(getWidgetInput(KEY_ION)).doubleValue();
				float relativeResponse = decimalFormat.parse(getWidgetInput(KEY_RELATIVE_RESPONSE)).floatValue();
				double uncertainty = decimalFormat.parse(getWidgetInput(KEY_UNCERTAINTY)).doubleValue();
				boolean use = Boolean.parseBoolean(getWidgetInput(KEY_USE));
				//
				IQuantitationSignal quantitationSignalMSD = new QuantitationSignal(ion, relativeResponse, uncertainty, use);
				quantitationSignalEntryEdit.setQuantitationSignalMSD(quantitationSignalMSD);
			} catch(ParseException e) {
				setErrorMessage("A value can't be parsed.");
				logger.warn(e);
			}
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		//
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		/*
		 * Default / existing values.
		 */
		String ion = "";
		String relativeResponse = "";
		String uncertainty = "";
		boolean use = true;
		//
		IQuantitationSignal quantitationSignalMSD = quantitationSignalEntryEdit.getQuantitationSignalMSD();
		if(quantitationSignalMSD != null) {
			ion = decimalFormat.format(quantitationSignalMSD.getSignal());
			relativeResponse = decimalFormat.format(quantitationSignalMSD.getRelativeResponse());
			uncertainty = decimalFormat.format(quantitationSignalMSD.getUncertainty());
			use = quantitationSignalMSD.isUse();
		}
		/*
		 * Text fields, ...
		 */
		createTextInput(composite, layoutData, KEY_ION, "TIC = 0", ion, true);
		createTextInput(composite, layoutData, KEY_RELATIVE_RESPONSE, "Max = TIC (" + IQuantitationSignal.ABSOLUTE_RESPONSE + ")", relativeResponse, true);
		createTextInput(composite, layoutData, KEY_UNCERTAINTY, "", uncertainty, true);
		createCheckInput(composite, layoutData, KEY_USE, "", use);
		validateInput();
		//
		return composite;
	}

	protected boolean validateInput() {

		boolean isValid = super.validateInput();
		/*
		 * If the result is valid, make additional checks.
		 */
		if(isValid) {
			/*
			 * Ion
			 */
			try {
				double value = decimalFormat.parse(getWidgetInput(KEY_ION)).doubleValue();
				if(value < 0) {
					setErrorMessage("Select a ion >= 0 (TIC = 0).");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The ion is not valid.");
				return false;
			}
			/*
			 * Relative Response
			 */
			try {
				float value = decimalFormat.parse(getWidgetInput(KEY_RELATIVE_RESPONSE)).floatValue();
				if(value <= 0 || value > IQuantitationSignal.ABSOLUTE_RESPONSE) {
					setErrorMessage("Select a relative response >= 0 and <= " + IQuantitationSignal.ABSOLUTE_RESPONSE + ".");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The relative response is not valid.");
				return false;
			}
			/*
			 * Uncertainty
			 */
			try {
				double value = decimalFormat.parse(getWidgetInput(KEY_UNCERTAINTY)).doubleValue();
				if(value < 0) {
					setErrorMessage("Select a uncertainty value >= 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The uncertainty value is not valid.");
				return false;
			}
		}
		return isValid;
	}
}
