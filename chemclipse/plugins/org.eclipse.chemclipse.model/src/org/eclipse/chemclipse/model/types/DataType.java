/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add MALDI support
 *******************************************************************************/
package org.eclipse.chemclipse.model.types;

import org.eclipse.chemclipse.processing.DataCategory;

public enum DataType {

	NONE, // Used e.g. as an initial value for the Scan Table
	AUTO_DETECT, // Auto-Detect
	MSD_NOMINAL, // Quadrupole, Ion Trap
	MSD_TANDEM, // MS/MS
	MSD_HIGHRES, // Orbitrap, TOF
	MSD, // mass selective data
	CSD, // current selective data
	WSD, // wavelength selective data
	ISD, // infrared selective data (FT-IR, Raman)
	TSD, // time selective data (IMS)
	XIR, // Infrared detectors, FTIR, NIR, MIR
	NMR, // Nuclear magnetic resonance
	CAL, // Retention Index Calibration
	PCR, // Polymerase Chain Reaction
	SEQ, // Sequences
	MTH, // Methods
	QDB, // Quantitation Databases
	MALDI, // MALDI-TOF MS
	MSD_DATABASE; // Mass Spectral Databases (*.msl, ...)

	public static DataType fromDataCategory(DataCategory category) {

		switch(category) {
			case MSD:
				return DataType.MSD;
			case WSD:
				return DataType.WSD;
			case CSD:
				return DataType.CSD;
			case ISD:
				return DataType.ISD;
			case FID:
			case NMR:
				return DataType.NMR;
			case MALDI:
				return DataType.MALDI;
			case MSD_DATABASE:
				return DataType.MSD_DATABASE;
			default:
				return DataType.AUTO_DETECT;
		}
	}

	public DataCategory toDataCategory() {

		switch(this) {
			case MSD:
				return DataCategory.MSD;
			case WSD:
				return DataCategory.WSD;
			case CSD:
				return DataCategory.CSD;
			case ISD:
				return DataCategory.ISD;
			case NMR:
				return DataCategory.NMR;
			case MALDI:
				return DataCategory.MALDI;
			case MSD_DATABASE:
				return DataCategory.MSD_DATABASE;
			default:
				return DataCategory.AUTO_DETECT;
		}
	}

	public static DataCategory[] convert(DataType[] dataTypes) {

		DataCategory[] categories = new DataCategory[dataTypes.length];
		for(int i = 0; i < categories.length; i++) {
			categories[i] = dataTypes[i].toDataCategory();
		}
		return categories;
	}

	public static DataType[] convert(DataCategory[] categories) {

		DataType[] dataTypes = new DataType[categories.length];
		for(int i = 0; i < categories.length; i++) {
			dataTypes[i] = fromDataCategory(categories[i]);
		}
		return dataTypes;
	}
}