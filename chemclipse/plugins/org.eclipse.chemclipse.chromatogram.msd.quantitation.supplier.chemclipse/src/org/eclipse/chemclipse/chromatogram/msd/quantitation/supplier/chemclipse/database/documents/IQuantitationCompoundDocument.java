/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntriesMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntryMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalsMSD;
import org.eclipse.chemclipse.database.documents.IDocument;

public interface IQuantitationCompoundDocument extends IDocument {

	String CLASS_NAME = "QuantitationCompound";
	/*
	 * Fields
	 */
	String FIELD_NAME = "Name";
	String FIELD_CHEMICAL_CLASS = "ChemicalClass";
	String FIELD_CALIBRATION_METHOD = "CalibrationMethod";
	String FIELD_USE_CROSS_ZERO = "UseCrossZero";
	String FIELD_USE_TIC = "UseTIC";
	String FIELD_CONCENTRATION_RESPONSE_ENTRIES = "ConcentrationResponseEntries";
	String FIELD_QUANTITATION_SIGNALS = "QuantitationSignals";
	String FIELD_CONCENTRATION_UNIT = "ConcentrationUnit";
	String FIELD_RETENTION_INDEX = "RetentionIndex";
	String FIELD_RI_NEGATIVE_DEVIATION = "RINegativeDeviation";
	String FIELD_RI_POSITIVE_DEVIATION = "RIPositiveDeviation";
	String FIELD_RETENTION_TIME = "RetentionTime";
	String FIELD_RT_NEGATIVE_DEVIATION = "RTNegativeDeviation";
	String FIELD_RT_POSITIVE_DEVIATION = "RTPositiveDeviation";
	//
	String FIELD_QUANTITATION_PEAK_IDS = "QuantitationPeakIds";

	//
	void setQuantitationCompound(IQuantitationCompoundMSD quantitationCompound);

	IQuantitationCompoundMSD getQuantitationCompound();

	/**
	 * Updates the quantitation compound. No explicit save is needed.
	 * 
	 * @param quantitationCompoundMSD
	 */
	void updateQuantitationCompound(IQuantitationCompoundMSD quantitationCompoundMSD);

	//
	String getName();

	String getChemicalClass();

	CalibrationMethod getCalibrationMethod();

	boolean isUseCrossZero();

	boolean isUseTIC();

	/*
	 * Concentration response entries
	 */
	IConcentrationResponseEntriesMSD getConcentrationResponseEntriesMSD();

	void addConcentrationResponseEntryMSD(IConcentrationResponseEntryMSD concentrationResponseEntryMSD);

	void removeConcentrationResponseEntryMSD(IConcentrationResponseEntryMSD concentrationResponseEntryMSD);

	void removeConcentrationResponseEntriesMSD(List<IConcentrationResponseEntryMSD> concentrationResponseEntriesMSD);

	void removeAllConcentrationResponseEntriesMSD();

	void updateConcentrationResponseEntries(IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD);

	/*
	 * Quantitation signals
	 */
	IQuantitationSignalsMSD getQuantitationSignalsMSD();

	void addQuantitationSignalMSD(IQuantitationSignalMSD quantitationSignalMSD);

	void removeQuantitationSignalMSD(IQuantitationSignalMSD quantitationSignalMSD);

	void removeQuantitationSignalsMSD(List<IQuantitationSignalMSD> quantitationSignalsMSD);

	void removeAllQuantitationSignalsMSD();

	void updateQuantitationSignalsMSD(IQuantitationSignalsMSD quantitationSignalsMSD);

	/*
	 * ppm, mg/ml, ...
	 */
	String getConcentrationUnit();

	/*
	 * Retention Index
	 */
	float getRetentionIndex();

	float getAllowedNegativeRetentionIndexDeviation();

	float getAllowedPositiveRetentionIndexDeviation();

	/**
	 * Returns the lower limit of the allowed retention index.
	 * 
	 * @return float
	 */
	float getLowerRetentionIndexLimit();

	/**
	 * Returns the upper limit of the allowed retention index.
	 * 
	 * @return float
	 */
	float getUpperRetentionIndexLimit();

	/*
	 * Retention Time
	 */
	int getRetentionTime();

	float getAllowedNegativeRetentionTimeDeviation();

	float getAllowedPositiveRetentionTimeDeviation();

	/**
	 * Returns the lower limit of the allowed retention time.
	 * 
	 * @return int
	 */
	int getLowerRetentionTimeLimit();

	/**
	 * Returns the upper limit of the allowed retention time.
	 * 
	 * @return int
	 */
	int getUpperRetentionTimeLimit();

	/*
	 * Quantitation peaks are used to create the concentration response and quantitation signal tables.
	 */
	void setQuantitationPeakIds(Set<Long> ids);

	Set<Long> getQuantitationPeakIds();
}
