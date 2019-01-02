/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.QuantCompoundListUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class QuantCompoundEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private QuantCompoundListUI tableViewer;
	private String column;
	private String[] calibrationMethods = new String[]{ //
			CalibrationMethod.LINEAR.toString(), //
			CalibrationMethod.QUADRATIC.toString(), //
			CalibrationMethod.AVERAGE.toString() //
	};

	public QuantCompoundEditingSupport(QuantCompoundListUI tableViewer, String column) {
		super(tableViewer);
		this.column = column;
		this.tableViewer = tableViewer;
		setCellEditor(column);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {

		return tableViewer.isEditEnabled();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object getValue(Object element) {

		if(element instanceof IQuantitationCompound) {
			IQuantitationCompound compound = (IQuantitationCompound)element;
			switch(column) {
				case QuantCompoundLabelProvider.NAME:
					return compound.getName();
				case QuantCompoundLabelProvider.CHEMICAL_CLASS:
					return compound.getChemicalClass();
				case QuantCompoundLabelProvider.CONCENTRATION_UNIT:
					return compound.getConcentrationUnit();
				case QuantCompoundLabelProvider.CALIBRATION_METHOD:
					String item = compound.getCalibrationMethod().toString();
					for(int i = 0; i < calibrationMethods.length; i++) {
						if(calibrationMethods[i].equals(item)) {
							return i;
						}
					}
					return 0;
				case QuantCompoundLabelProvider.CROSS_ZERO:
					return compound.isCrossZero();
				case QuantCompoundLabelProvider.USE_TIC:
					return compound.isUseTIC();
				case QuantCompoundLabelProvider.RETENTION_TIME:
					return Double.toString(compound.getRetentionTimeWindow().getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				case QuantCompoundLabelProvider.RETENTION_TIME_LOWER:
					return Double.toString(compound.getRetentionTimeWindow().getAllowedNegativeDeviation() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				case QuantCompoundLabelProvider.RETENTION_TIME_UPPER:
					return Double.toString(compound.getRetentionTimeWindow().getAllowedPositiveDeviation() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				case QuantCompoundLabelProvider.RETENTION_INDEX:
					return Float.toString(compound.getRetentionIndexWindow().getRetentionIndex());
				case QuantCompoundLabelProvider.RETENTION_INDEX_LOWER:
					return Float.toString(compound.getRetentionIndexWindow().getAllowedNegativeDeviation());
				case QuantCompoundLabelProvider.RETENTION_INDEX_UPPER:
					return Float.toString(compound.getRetentionIndexWindow().getAllowedPositiveDeviation());
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IQuantitationCompound) {
			IQuantitationCompound compound = (IQuantitationCompound)element;
			switch(column) {
				case QuantCompoundLabelProvider.NAME:
					String name = (String)value;
					if(!tableViewer.containsName(name)) {
						compound.setName(name);
					}
					break;
				case QuantCompoundLabelProvider.CHEMICAL_CLASS:
					compound.setChemicalClass((String)value);
					break;
				case QuantCompoundLabelProvider.CONCENTRATION_UNIT:
					compound.setConcentrationUnit((String)value);
					break;
				case QuantCompoundLabelProvider.CALIBRATION_METHOD:
					String calibrationMethod = calibrationMethods[(int)value];
					compound.setCalibrationMethod(CalibrationMethod.valueOf(calibrationMethod));
					break;
				case QuantCompoundLabelProvider.CROSS_ZERO:
					compound.setUseCrossZero((boolean)value);
					break;
				case QuantCompoundLabelProvider.USE_TIC:
					compound.setUseTIC((boolean)value);
					break;
				case QuantCompoundLabelProvider.RETENTION_TIME:
					double retentionTime = getValue(value, 0.0d);
					if(retentionTime >= 0) {
						compound.getRetentionTimeWindow().setRetentionTime((int)(retentionTime * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					}
					break;
				case QuantCompoundLabelProvider.RETENTION_TIME_LOWER:
					double allowedNegativeDeviationRT = getValue(value, 0.0d);
					if(allowedNegativeDeviationRT >= 0) {
						compound.getRetentionTimeWindow().setAllowedNegativeDeviation((int)(allowedNegativeDeviationRT * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					}
					break;
				case QuantCompoundLabelProvider.RETENTION_TIME_UPPER:
					double allowedPositiveDeviationRT = getValue(value, 0.0d);
					if(allowedPositiveDeviationRT >= 0) {
						compound.getRetentionTimeWindow().setAllowedPositiveDeviation((int)(allowedPositiveDeviationRT * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					}
					break;
				case QuantCompoundLabelProvider.RETENTION_INDEX:
					float retentionIndex = getValue(value, 0.0f);
					if(retentionIndex >= 0) {
						compound.getRetentionIndexWindow().setRetentionIndex(retentionIndex);
					}
					break;
				case QuantCompoundLabelProvider.RETENTION_INDEX_LOWER:
					float allowedNegativeDeviationRI = getValue(value, 0.0f);
					if(allowedNegativeDeviationRI >= 0) {
						compound.getRetentionIndexWindow().setAllowedNegativeDeviation(allowedNegativeDeviationRI);
					}
					break;
				case QuantCompoundLabelProvider.RETENTION_INDEX_UPPER:
					float allowedPositiveDeviationRI = getValue(value, 0.0f);
					if(allowedPositiveDeviationRI >= 0) {
						compound.getRetentionIndexWindow().setAllowedPositiveDeviation(allowedPositiveDeviationRI);
					}
					break;
			}
			tableViewer.refresh();
		}
	}

	private float getValue(Object value, float def) {

		float result = def;
		if(value instanceof String) {
			try {
				result = Float.parseFloat((String)value);
			} catch(NumberFormatException e) {
				//
			}
		}
		return result;
	}

	private double getValue(Object value, double def) {

		double result = def;
		if(value instanceof String) {
			try {
				result = Double.parseDouble((String)value);
			} catch(NumberFormatException e) {
				//
			}
		}
		return result;
	}

	private void setCellEditor(String column) {

		switch(column) {
			case QuantCompoundLabelProvider.CROSS_ZERO:
			case QuantCompoundLabelProvider.USE_TIC:
				this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
				break;
			case QuantCompoundLabelProvider.CALIBRATION_METHOD:
				this.cellEditor = new ComboBoxCellEditor(tableViewer.getTable(), //
						new String[]{ //
								CalibrationMethod.LINEAR.toString(), //
								CalibrationMethod.QUADRATIC.toString(), //
								CalibrationMethod.AVERAGE.toString() //
						}, //
						SWT.READ_ONLY);
				break;
			default:
				this.cellEditor = new TextCellEditor(tableViewer.getTable());
				break;
		}
	}
}
