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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.quantitation.ConcentrationResponseEntriesMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.ConcentrationResponseEntryMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntriesMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntryMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalsMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.QuantitationSignalMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.QuantitationSignalsMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.database.documents.AbstractDocument;

public class QuantitationCompoundDocument extends AbstractDocument implements IQuantitationCompoundDocument {

	/**
	 * Renew this uid on each change.
	 */
	private static final long serialVersionUID = -8636901036699807227L;
	//
	private static final String VALUE_DELIMITER = ";";

	public QuantitationCompoundDocument() {
		super(CLASS_NAME);
	}

	public QuantitationCompoundDocument(ODocument document) {
		super(document);
	}

	@Override
	public void setFields() {

		setQuantitationCompound(getQuantitationCompound());
		setQuantitationPeakIds(getQuantitationPeakIds());
	}

	@Override
	public void setQuantitationCompound(IQuantitationCompoundMSD quantitationCompoundMSD) {

		setName(quantitationCompoundMSD.getName());
		setChemicalClass(quantitationCompoundMSD.getChemicalClass());
		setCalibrationMethod(quantitationCompoundMSD.getCalibrationMethod());
		setConcentrationUnit(quantitationCompoundMSD.getConcentrationUnit());
		setUseCrossZero(quantitationCompoundMSD.isCrossZero());
		setUseTIC(quantitationCompoundMSD.isUseTIC());
		//
		IConcentrationResponseEntriesMSD concentrationResponseEntries = quantitationCompoundMSD.getConcentrationResponseEntries();
		setConcentrationResponseEntries(concentrationResponseEntries);
		//
		IQuantitationSignalsMSD quantitationSignals = quantitationCompoundMSD.getQuantitationSignals();
		setQuantitationSignals(quantitationSignals);
		//
		IRetentionIndexWindow retentionIndexWindow = quantitationCompoundMSD.getRetentionIndexWindow();
		setRetentionIndex(retentionIndexWindow.getRetentionIndex());
		setAllowedNegativeRetentionIndexDeviation(retentionIndexWindow.getAllowedNegativeDeviation());
		setAllowedPositiveRetentionIndexDeviation(retentionIndexWindow.getAllowedPositiveDeviation());
		//
		IRetentionTimeWindow retentionTimeWindow = quantitationCompoundMSD.getRetentionTimeWindow();
		setRetentionTime(retentionTimeWindow.getRetentionTime());
		setAllowedNegativeRetentionTimeDeviation(retentionTimeWindow.getAllowedNegativeDeviation());
		setAllowedPositiveRetentionTimeDeviation(retentionTimeWindow.getAllowedPositiveDeviation());
	}

	@Override
	public IQuantitationCompoundMSD getQuantitationCompound() {

		//
		IQuantitationCompoundMSD quantitationCompound = new QuantitationCompoundMSD(getName(), getConcentrationUnit(), getRetentionTime());
		quantitationCompound.setChemicalClass(getChemicalClass());
		quantitationCompound.setCalibrationMethod(getCalibrationMethod());
		quantitationCompound.setUseCrossZero(isUseCrossZero());
		quantitationCompound.setUseTIC(isUseTIC());
		//
		IConcentrationResponseEntriesMSD concentrationResponseEntries = quantitationCompound.getConcentrationResponseEntries();
		initConcentrationResponseEntries(concentrationResponseEntries);
		//
		IQuantitationSignalsMSD quantitationSignals = quantitationCompound.getQuantitationSignals();
		initQuantitationSignals(quantitationSignals);
		//
		IRetentionIndexWindow retentionIndexWindow = quantitationCompound.getRetentionIndexWindow();
		retentionIndexWindow.setRetentionIndex(getRetentionIndex());
		retentionIndexWindow.setAllowedNegativeDeviation(getAllowedNegativeRetentionIndexDeviation());
		retentionIndexWindow.setAllowedPositiveDeviation(getAllowedPositiveRetentionIndexDeviation());
		//
		IRetentionTimeWindow retentionTimeWindow = quantitationCompound.getRetentionTimeWindow();
		retentionTimeWindow.setAllowedNegativeDeviation(getAllowedNegativeRetentionTimeDeviation());
		retentionTimeWindow.setAllowedPositiveDeviation(getAllowedPositiveRetentionTimeDeviation());
		//
		return quantitationCompound;
	}

	@Override
	public void updateQuantitationCompound(IQuantitationCompoundMSD quantitationCompoundMSD) {

		setQuantitationCompound(quantitationCompoundMSD);
		save();
	}

	@Override
	public void setQuantitationPeakIds(Set<Long> ids) {

		field(FIELD_QUANTITATION_PEAK_IDS, ids, OType.EMBEDDEDSET);
	}

	@Override
	public Set<Long> getQuantitationPeakIds() {

		return getIds(FIELD_QUANTITATION_PEAK_IDS);
	}

	// ----------------------------------------------------------------------------
	@Override
	public IConcentrationResponseEntriesMSD getConcentrationResponseEntriesMSD() {

		IConcentrationResponseEntriesMSD concentrationResponseEntries = new ConcentrationResponseEntriesMSD();
		initConcentrationResponseEntries(concentrationResponseEntries);
		return concentrationResponseEntries;
	}

	@Override
	public void addConcentrationResponseEntryMSD(IConcentrationResponseEntryMSD concentrationResponseEntryMSD) {

		IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD = getConcentrationResponseEntriesMSD();
		concentrationResponseEntriesMSD.add(concentrationResponseEntryMSD);
		updateConcentrationResponseEntries(concentrationResponseEntriesMSD);
	}

	@Override
	public void removeConcentrationResponseEntryMSD(IConcentrationResponseEntryMSD concentrationResponseEntryMSD) {

		IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD = getConcentrationResponseEntriesMSD();
		concentrationResponseEntriesMSD.remove(concentrationResponseEntryMSD);
		updateConcentrationResponseEntries(concentrationResponseEntriesMSD);
	}

	@Override
	public void removeConcentrationResponseEntriesMSD(List<IConcentrationResponseEntryMSD> concentrationResponseEntriesMSD) {

		IConcentrationResponseEntriesMSD concentrationResponseEntries = getConcentrationResponseEntriesMSD();
		concentrationResponseEntries.removeAll(concentrationResponseEntriesMSD);
		updateConcentrationResponseEntries(concentrationResponseEntries);
	}

	@Override
	public void removeAllConcentrationResponseEntriesMSD() {

		IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD = new ConcentrationResponseEntriesMSD();
		updateConcentrationResponseEntries(concentrationResponseEntriesMSD);
	}

	@Override
	public void updateConcentrationResponseEntries(IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD) {

		setConcentrationResponseEntries(concentrationResponseEntriesMSD);
		save();
	}

	private void initConcentrationResponseEntries(IConcentrationResponseEntriesMSD concentrationResponseEntries) {

		Object object = getObject(FIELD_CONCENTRATION_RESPONSE_ENTRIES);
		if(object != null && object instanceof List) {
			@SuppressWarnings("rawtypes")
			List list = (List)object;
			for(Object entry : list) {
				String[] values = ((String)entry).split(VALUE_DELIMITER);
				if(values.length == 3) {
					double ion = Double.parseDouble(values[0]);
					double concentration = Double.parseDouble(values[1]);
					double response = Double.parseDouble(values[2]);
					//
					IConcentrationResponseEntryMSD concentrationResponseEntry = new ConcentrationResponseEntryMSD(ion, concentration, response);
					concentrationResponseEntries.add(concentrationResponseEntry);
				}
			}
		}
	}

	private void setConcentrationResponseEntries(IConcentrationResponseEntriesMSD concentrationResponseEntries) {

		List<String> concentrationResponseList = new ArrayList<String>();
		for(IConcentrationResponseEntryMSD responseEntry : concentrationResponseEntries.getList()) {
			StringBuilder builder = new StringBuilder();
			builder.append(responseEntry.getIon());
			builder.append(VALUE_DELIMITER);
			builder.append(responseEntry.getConcentration());
			builder.append(VALUE_DELIMITER);
			builder.append(responseEntry.getResponse());
			//
			concentrationResponseList.add(builder.toString());
		}
		//
		field(FIELD_CONCENTRATION_RESPONSE_ENTRIES, concentrationResponseList, OType.EMBEDDEDLIST);
	}

	// ----------------------------------------------------------------------------
	@Override
	public String getName() {

		return getString(FIELD_NAME);
	}

	private void setName(String name) {

		field(FIELD_NAME, name, OType.STRING);
	}

	@Override
	public String getChemicalClass() {

		return getString(FIELD_CHEMICAL_CLASS);
	}

	private void setChemicalClass(String chemicalClass) {

		field(FIELD_CHEMICAL_CLASS, chemicalClass, OType.STRING);
	}

	@Override
	public CalibrationMethod getCalibrationMethod() {

		return CalibrationMethod.valueOf(getString(FIELD_CALIBRATION_METHOD));
	}

	private void setCalibrationMethod(CalibrationMethod calibrationMethod) {

		field(FIELD_CALIBRATION_METHOD, calibrationMethod.toString(), OType.STRING);
	}

	@Override
	public boolean isUseCrossZero() {

		return getBoolean(FIELD_USE_CROSS_ZERO);
	}

	private void setUseCrossZero(boolean useCrossZero) {

		field(FIELD_USE_CROSS_ZERO, useCrossZero, OType.BOOLEAN);
	}

	@Override
	public boolean isUseTIC() {

		return getBoolean(FIELD_USE_TIC);
	}

	private void setUseTIC(boolean useTIC) {

		field(FIELD_USE_TIC, useTIC, OType.BOOLEAN);
	}

	// ----------------------------------------------------------------------------
	@Override
	public IQuantitationSignalsMSD getQuantitationSignalsMSD() {

		IQuantitationSignalsMSD quantitationSignals = new QuantitationSignalsMSD();
		initQuantitationSignals(quantitationSignals);
		return quantitationSignals;
	}

	@Override
	public void addQuantitationSignalMSD(IQuantitationSignalMSD quantitationSignalMSD) {

		IQuantitationSignalsMSD quantitationSignalsMSD = getQuantitationSignalsMSD();
		quantitationSignalsMSD.add(quantitationSignalMSD);
		updateQuantitationSignalsMSD(quantitationSignalsMSD);
	}

	@Override
	public void removeQuantitationSignalMSD(IQuantitationSignalMSD quantitationSignalMSD) {

		IQuantitationSignalsMSD quantitationSignalsMSD = getQuantitationSignalsMSD();
		quantitationSignalsMSD.remove(quantitationSignalMSD);
		updateQuantitationSignalsMSD(quantitationSignalsMSD);
	}

	@Override
	public void removeQuantitationSignalsMSD(List<IQuantitationSignalMSD> quantitationSignalsMSD) {

		IQuantitationSignalsMSD quantitationSignals = getQuantitationSignalsMSD();
		quantitationSignals.removeAll(quantitationSignalsMSD);
		updateQuantitationSignalsMSD(quantitationSignals);
	}

	@Override
	public void removeAllQuantitationSignalsMSD() {

		IQuantitationSignalsMSD quantitationSignalsMSD = new QuantitationSignalsMSD();
		updateQuantitationSignalsMSD(quantitationSignalsMSD);
	}

	@Override
	public void updateQuantitationSignalsMSD(IQuantitationSignalsMSD quantitationSignalsMSD) {

		setQuantitationSignals(quantitationSignalsMSD);
		save();
	}

	private void initQuantitationSignals(IQuantitationSignalsMSD quantitationSignalsMSD) {

		Object object = getObject(FIELD_QUANTITATION_SIGNALS);
		if(object != null && object instanceof List) {
			@SuppressWarnings("rawtypes")
			List list = (List)object;
			for(Object entry : list) {
				String[] values = ((String)entry).split(VALUE_DELIMITER);
				if(values.length == 4) {
					double ion = Double.parseDouble(values[0]);
					float relativeResponse = Float.parseFloat(values[1]);
					double uncertainty = Double.parseDouble(values[2]);
					boolean use = Boolean.parseBoolean(values[3]);
					//
					IQuantitationSignalMSD quantitationSignal = new QuantitationSignalMSD(ion, relativeResponse, uncertainty, use);
					quantitationSignalsMSD.add(quantitationSignal);
				}
			}
		}
	}

	private void setQuantitationSignals(IQuantitationSignalsMSD quantitationSignalsMSD) {

		List<String> quantitationSignalList = new ArrayList<String>();
		for(IQuantitationSignalMSD quantitationSignal : quantitationSignalsMSD.getList()) {
			StringBuilder builder = new StringBuilder();
			builder.append(quantitationSignal.getIon());
			builder.append(VALUE_DELIMITER);
			builder.append(quantitationSignal.getRelativeResponse());
			builder.append(VALUE_DELIMITER);
			builder.append(quantitationSignal.getUncertainty());
			builder.append(VALUE_DELIMITER);
			builder.append(quantitationSignal.isUse());
			//
			quantitationSignalList.add(builder.toString());
		}
		//
		field(FIELD_QUANTITATION_SIGNALS, quantitationSignalList, OType.EMBEDDEDLIST);
	}

	// ----------------------------------------------------------------------------
	@Override
	public String getConcentrationUnit() {

		return getString(FIELD_CONCENTRATION_UNIT);
	}

	private void setConcentrationUnit(String concentrationUnit) {

		field(FIELD_CONCENTRATION_UNIT, concentrationUnit, OType.STRING);
	}

	@Override
	public float getRetentionIndex() {

		return getFloat(FIELD_RETENTION_INDEX);
	}

	private void setRetentionIndex(float retentionIndex) {

		field(FIELD_RETENTION_INDEX, retentionIndex, OType.FLOAT);
	}

	@Override
	public float getAllowedNegativeRetentionIndexDeviation() {

		return getFloat(FIELD_RI_NEGATIVE_DEVIATION);
	}

	private void setAllowedNegativeRetentionIndexDeviation(float allowedNegativeDeviationRI) {

		field(FIELD_RI_NEGATIVE_DEVIATION, allowedNegativeDeviationRI, OType.FLOAT);
	}

	@Override
	public float getAllowedPositiveRetentionIndexDeviation() {

		return getFloat(FIELD_RI_POSITIVE_DEVIATION);
	}

	private void setAllowedPositiveRetentionIndexDeviation(float allowedPositiveDeviationRI) {

		field(FIELD_RI_POSITIVE_DEVIATION, allowedPositiveDeviationRI, OType.FLOAT);
	}

	@Override
	public int getRetentionTime() {

		return getInteger(FIELD_RETENTION_TIME);
	}

	private void setRetentionTime(int retentionTime) {

		field(FIELD_RETENTION_TIME, retentionTime, OType.INTEGER);
	}

	@Override
	public float getAllowedNegativeRetentionTimeDeviation() {

		return getFloat(FIELD_RT_NEGATIVE_DEVIATION);
	}

	private void setAllowedNegativeRetentionTimeDeviation(float allowedNegativeDeviationRT) {

		field(FIELD_RT_NEGATIVE_DEVIATION, allowedNegativeDeviationRT, OType.FLOAT);
	}

	@Override
	public float getAllowedPositiveRetentionTimeDeviation() {

		return getFloat(FIELD_RT_NEGATIVE_DEVIATION);
	}

	private void setAllowedPositiveRetentionTimeDeviation(float allowedPositiveDeviationRT) {

		field(FIELD_RT_POSITIVE_DEVIATION, allowedPositiveDeviationRT, OType.FLOAT);
	}

	// ----------------------------------------------------------------------------
	@Override
	public int getLowerRetentionTimeLimit() {

		return getRetentionTime() - (int)getAllowedNegativeRetentionTimeDeviation();
	}

	@Override
	public int getUpperRetentionTimeLimit() {

		return getRetentionTime() + (int)getAllowedPositiveRetentionTimeDeviation();
	}

	@Override
	public float getLowerRetentionIndexLimit() {

		return getRetentionIndex() - getAllowedNegativeRetentionIndexDeviation();
	}

	@Override
	public float getUpperRetentionIndexLimit() {

		return getRetentionIndex() + getAllowedPositiveRetentionIndexDeviation();
	}
}
