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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class QuantitationCompoundEditDialog extends AbstractEntryEditDialog {

	private static final Logger logger = Logger.getLogger(QuantitationCompoundEditDialog.class);
	//
	private static final String KEY_NAME = "Name";
	private static final String KEY_CHEMICAL_CLASS = "Chemical Class";
	private static final String KEY_CONCENTRATION_UNIT = "Concentration Unit";
	private static final String KEY_CALIBRATION_METHOD = "Calibration Method";
	private static final String KEY_USE_CROSS_ZERO = "Cross Zero";
	private static final String KEY_USE_TIC = "TIC";
	private static final String KEY_RETENTION_TIME = "Retention Time (RT)";
	private static final String KEY_RT_NEGATIVE = "(RT) -";
	private static final String KEY_RT_POSITIVE = "(RT) +";
	private static final String KEY_RETENTION_INDEX = "Retention Index";
	private static final String KEY_RI_NEGATIVE = "(RI) -";
	private static final String KEY_RI_POSITIVE = "(RI) +";
	//
	private QuantitationCompoundEntryEdit quantitationCompoundEntryEdit;
	//
	private DecimalFormat decimalFormat;
	private IQuantitationDatabase database;
	private boolean isNewCompound;

	public QuantitationCompoundEditDialog(Shell parentShell, QuantitationCompoundEntryEdit quantitationCompoundEntryEdit, String title, IQuantitationDatabase database, boolean isNewCompound) {
		super(parentShell, title, (isNewCompound) ? "Create a quantitation compound." : "Edit a quantitation compound.");
		this.quantitationCompoundEntryEdit = quantitationCompoundEntryEdit;
		if(database != null) {
			this.database = database;
		} else {
			setErrorMessage("The selected database is not valid.");
		}
		this.isNewCompound = isNewCompound;
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
			//
			try {
				String name = getWidgetInput(KEY_NAME);
				String chemicalClass = getWidgetInput(KEY_CHEMICAL_CLASS);
				String concentrationUnit = getWidgetInput(KEY_CONCENTRATION_UNIT);
				CalibrationMethod calibrationMethod = CalibrationMethod.valueOf(getWidgetInput(KEY_CALIBRATION_METHOD));
				boolean useCrossZero = Boolean.parseBoolean(getWidgetInput(KEY_USE_CROSS_ZERO));
				boolean useTIC = Boolean.parseBoolean(getWidgetInput(KEY_USE_TIC));
				int retentionTime = (int)(decimalFormat.parse(getWidgetInput(KEY_RETENTION_TIME)).doubleValue() * IChromatogram.MINUTE_CORRELATION_FACTOR);
				float allowedNegativeDeviationRT = (int)(decimalFormat.parse(getWidgetInput(KEY_RT_NEGATIVE)).doubleValue() * IChromatogram.MINUTE_CORRELATION_FACTOR);
				float allowedPositiveDeviationRT = (int)(decimalFormat.parse(getWidgetInput(KEY_RT_POSITIVE)).doubleValue() * IChromatogram.MINUTE_CORRELATION_FACTOR);
				//
				float retentionIndex = decimalFormat.parse(getWidgetInput(KEY_RETENTION_INDEX)).floatValue();
				float allowedNegativeDeviationRI = decimalFormat.parse(getWidgetInput(KEY_RI_NEGATIVE)).floatValue();
				float allowedPositiveDeviationRI = decimalFormat.parse(getWidgetInput(KEY_RI_POSITIVE)).floatValue();
				//
				IQuantitationCompound quantitationCompoundMSD = new QuantitationCompoundMSD(name, concentrationUnit, retentionTime);
				quantitationCompoundMSD.setUseTIC(useTIC);
				quantitationCompoundMSD.setCalibrationMethod(calibrationMethod);
				quantitationCompoundMSD.setUseCrossZero(useCrossZero);
				quantitationCompoundMSD.setChemicalClass(chemicalClass);
				//
				IRetentionTimeWindow retentionTimeWindow = quantitationCompoundMSD.getRetentionTimeWindow();
				retentionTimeWindow.setRetentionTime(retentionTime);
				retentionTimeWindow.setAllowedNegativeDeviation(allowedNegativeDeviationRT);
				retentionTimeWindow.setAllowedPositiveDeviation(allowedPositiveDeviationRT);
				//
				IRetentionIndexWindow retentionIndexWindow = quantitationCompoundMSD.getRetentionIndexWindow();
				retentionIndexWindow.setRetentionIndex(retentionIndex);
				retentionIndexWindow.setAllowedNegativeDeviation(allowedNegativeDeviationRI);
				retentionIndexWindow.setAllowedPositiveDeviation(allowedPositiveDeviationRI);
				//
				quantitationCompoundEntryEdit.setQuantitationCompoundMSD(quantitationCompoundMSD);
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
		String name = "";
		String chemicalClass = "";
		String concentrationUnit = "";
		String calibrationMethod = CalibrationMethod.LINEAR.toString();
		boolean useCrossZero = true;
		boolean useTIC = true;
		//
		String retentionTime = "";
		String allowedNegativeDeviationRT = decimalFormat.format(1500.0d / IChromatogram.MINUTE_CORRELATION_FACTOR);
		String allowedPositiveDeviationRT = decimalFormat.format(1500.0d / IChromatogram.MINUTE_CORRELATION_FACTOR);
		//
		String retentionIndex = decimalFormat.format(0);
		String allowedNegativeDeviationRI = decimalFormat.format(5);
		String allowedPositiveDeviationRI = decimalFormat.format(5);
		//
		IQuantitationCompound quantitationCompoundMSD = quantitationCompoundEntryEdit.getQuantitationCompound();
		if(quantitationCompoundMSD != null) {
			name = quantitationCompoundMSD.getName();
			chemicalClass = quantitationCompoundMSD.getChemicalClass();
			concentrationUnit = quantitationCompoundMSD.getConcentrationUnit();
			calibrationMethod = quantitationCompoundMSD.getCalibrationMethod().toString();
			useCrossZero = quantitationCompoundMSD.isCrossZero();
			useTIC = quantitationCompoundMSD.isUseTIC();
			//
			retentionTime = decimalFormat.format(quantitationCompoundMSD.getRetentionTimeWindow().getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
			allowedNegativeDeviationRT = decimalFormat.format(quantitationCompoundMSD.getRetentionTimeWindow().getAllowedNegativeDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
			allowedPositiveDeviationRT = decimalFormat.format(quantitationCompoundMSD.getRetentionTimeWindow().getAllowedPositiveDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
			//
			retentionIndex = decimalFormat.format(quantitationCompoundMSD.getRetentionIndexWindow().getRetentionIndex());
			allowedNegativeDeviationRI = decimalFormat.format(quantitationCompoundMSD.getRetentionIndexWindow().getAllowedNegativeDeviation());
			allowedPositiveDeviationRI = decimalFormat.format(quantitationCompoundMSD.getRetentionIndexWindow().getAllowedPositiveDeviation());
		}
		/*
		 * Text fields, ...
		 */
		createTextInput(composite, layoutData, KEY_NAME, "", name, isNewCompound);
		createTextInput(composite, layoutData, KEY_CHEMICAL_CLASS, "", chemicalClass, true);
		createTextInput(composite, layoutData, KEY_CONCENTRATION_UNIT, "", concentrationUnit, true);
		createEnumInput(composite, layoutData, KEY_CALIBRATION_METHOD, "", getCalibrationMethodItems(), calibrationMethod);
		createCheckInput(composite, layoutData, KEY_USE_CROSS_ZERO, "", useCrossZero);
		createCheckInput(composite, layoutData, KEY_USE_TIC, "", useTIC);
		createTextInput(composite, layoutData, KEY_RETENTION_TIME, "minutes", retentionTime, true);
		createTextInput(composite, layoutData, KEY_RT_NEGATIVE, "minutes", allowedNegativeDeviationRT, true);
		createTextInput(composite, layoutData, KEY_RT_POSITIVE, "minutes", allowedPositiveDeviationRT, true);
		createTextInput(composite, layoutData, KEY_RETENTION_INDEX, "", retentionIndex, true);
		createTextInput(composite, layoutData, KEY_RI_NEGATIVE, "", allowedNegativeDeviationRI, true);
		createTextInput(composite, layoutData, KEY_RI_POSITIVE, "", allowedPositiveDeviationRI, true);
		//
		validateInput();
		return composite;
	}

	private String[] getCalibrationMethodItems() {

		CalibrationMethod[] calibrationMethods = CalibrationMethod.values();
		List<String> items = new ArrayList<>();
		//
		for(CalibrationMethod calibrationMethod : calibrationMethods) {
			if(!calibrationMethod.equals(CalibrationMethod.ISTD)) {
				items.add(calibrationMethod.toString());
			}
		}
		return items.toArray(new String[items.size()]);
	}

	protected boolean validateInput() {

		boolean isValid = super.validateInput();
		/*
		 * If the result is valid, make additional checks.
		 */
		if(isValid) {
			/*
			 * Database check
			 */
			if(database != null && isNewCompound) {
				String name = getWidgetInput(KEY_NAME);
				for(IQuantitationCompound quantitationCompound : database) {
					if(quantitationCompound.getName().equals(name)) {
						setErrorMessage("The compound is already available.");
						return false;
					}
				}
			}
			/*
			 * Concentration Unit
			 */
			String concentrationUnit = getWidgetInput(KEY_CONCENTRATION_UNIT);
			if(concentrationUnit == null || concentrationUnit.equals("")) {
				setErrorMessage("The concentration unit is not valid.");
				return false;
			}
			/*
			 * Retention Time
			 */
			try {
				double retentionTime = decimalFormat.parse(getWidgetInput(KEY_RETENTION_TIME)).doubleValue();
				if(retentionTime <= 0) {
					setErrorMessage("Select a retention time > 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The retention time is not valid.");
				return false;
			}
			/*
			 * Deviation Retention Time Negative
			 */
			try {
				double value = decimalFormat.parse(getWidgetInput(KEY_RT_NEGATIVE)).doubleValue();
				if(value < 0) {
					setErrorMessage("Select an allowed negative retention time deviation >= 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The allowed negative retention time deviation is not valid.");
				return false;
			}
			/*
			 * Deviation Retention Time Positive
			 */
			try {
				double value = decimalFormat.parse(getWidgetInput(KEY_RT_POSITIVE)).doubleValue();
				if(value < 0) {
					setErrorMessage("Select an allowed positive retention time deviation >= 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The allowed positive retention time deviation is not valid.");
				return false;
			}
			/*
			 * Retention Index
			 */
			try {
				float retentionIndex = decimalFormat.parse(getWidgetInput(KEY_RETENTION_INDEX)).floatValue();
				if(retentionIndex < 0) {
					setErrorMessage("Select a retention index >= 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The retention index is not valid.");
				return false;
			}
			/*
			 * Deviation Retention Index Negative
			 */
			try {
				float value = decimalFormat.parse(getWidgetInput(KEY_RI_NEGATIVE)).floatValue();
				if(value < 0) {
					setErrorMessage("Select an allowed negative retention index deviation >= 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The allowed negative retention index deviation is not valid.");
				return false;
			}
			/*
			 * Deviation Retention Index Positive
			 */
			try {
				float value = decimalFormat.parse(getWidgetInput(KEY_RI_POSITIVE)).floatValue();
				if(value < 0) {
					setErrorMessage("Select an allowed positive retention index deviation >= 0.");
					return false;
				}
			} catch(ParseException e) {
				setErrorMessage("The allowed positive retention index deviation is not valid.");
				return false;
			}
		}
		return isValid;
	}
}
