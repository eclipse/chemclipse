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

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public interface IQuantitationPeakDocument {

	String CLASS_NAME = "QuantitationPeak";
	/*
	 * Fields
	 */
	String FIELD_CONCENTRATION = "Concentration";
	String FIELD_CONCENTRATION_UNIT = "ConcentrationUnit";
	String FIELD_PEAK_MSD = "PeakMSD";
	String FIELD_IONS = "Ions"; // Is used to store the mass spectrum of the peak.
	String FIELD_INTENSITY_VALUES = "IntensityValues"; // Is used to store the intensity values of the peak.
	String FIELD_INTEGRATION_VALUES = "IntegrationValues"; // Is used to store the integration values of the peak.

	long getDocumentId();

	void setConcentration(double concentration);

	double getConcentration();

	void setConcentrationUnit(String concentrationUnit);

	String getConcentrationUnit();

	/**
	 * Returns the peak. May return null.
	 * 
	 * @return {@link IPeakMSD}
	 */
	IPeakMSD getPeakMSD();

	void setPeakMSD(IPeakMSD peakMSD);
}
