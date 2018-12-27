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

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller.ConcentrationResponseEntryEdit;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.ConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ConcentrationResponseEntryEditDialog extends AbstractEntryEditDialog {

	private static final Logger logger = Logger.getLogger(ConcentrationResponseEntryEditDialog.class);
	//
	private static final String KEY_ION = "Ion";
	private static final String KEY_CONCENTRATION = "Concentration";
	private static final String KEY_RESPONSE = "Response";
	//
	private ConcentrationResponseEntryEdit concentrationResponseEntryEdit;
	//
	private DecimalFormat decimalFormat;

	public ConcentrationResponseEntryEditDialog(Shell parentShell, ConcentrationResponseEntryEdit concentrationResponseEntryEdit, String title) {
		super(parentShell, title, "Create/Edit a concentration response entry.");
		this.concentrationResponseEntryEdit = concentrationResponseEntryEdit;
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
				double concentration = decimalFormat.parse(getWidgetInput(KEY_CONCENTRATION)).doubleValue();
				double response = decimalFormat.parse(getWidgetInput(KEY_RESPONSE)).doubleValue();
				IConcentrationResponseEntry concentrationResponseEntryMSD = new ConcentrationResponseEntry(ion, concentration, response);
				concentrationResponseEntryEdit.setConcentrationResponseEntryMSD(concentrationResponseEntryMSD);
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
		String concentration = "";
		String response = "";
		//
		IConcentrationResponseEntry concentrationResponseEntryMSD = concentrationResponseEntryEdit.getConcentrationResponseEntryMSD();
		if(concentrationResponseEntryMSD != null) {
			ion = decimalFormat.format(concentrationResponseEntryMSD.getSignal());
			concentration = decimalFormat.format(concentrationResponseEntryMSD.getConcentration());
			response = decimalFormat.format(concentrationResponseEntryMSD.getResponse());
		}
		/*
		 * Text fields, ...
		 */
		createTextInput(composite, layoutData, KEY_ION, "TIC = 0", ion, true);
		createTextInput(composite, layoutData, KEY_CONCENTRATION, concentrationResponseEntryEdit.getConcentrationUnit(), concentration, true);
		createTextInput(composite, layoutData, KEY_RESPONSE, "", response, true);
		//
		validateInput();
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
			 * Concentration
			 */
			try {
				double value = decimalFormat.parse(getWidgetInput(KEY_CONCENTRATION)).doubleValue();
				if(value <= 0) {
					setErrorMessage("Select a concentration > 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The concentration is not valid.");
				return false;
			}
			/*
			 * Response
			 */
			try {
				double value = decimalFormat.parse(getWidgetInput(KEY_RESPONSE)).doubleValue();
				if(value <= 0) {
					setErrorMessage("Select a response > 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The response is not valid.");
				return false;
			}
		}
		return isValid;
	}
}
