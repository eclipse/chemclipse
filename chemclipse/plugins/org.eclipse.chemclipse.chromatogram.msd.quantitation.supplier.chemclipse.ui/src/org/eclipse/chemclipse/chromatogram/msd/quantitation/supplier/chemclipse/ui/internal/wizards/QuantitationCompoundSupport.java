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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;
import org.eclipse.chemclipse.xxd.model.quantitation.QuantitationCompound;

public class QuantitationCompoundSupport {

	public IQuantitationCompound create(IPeak peak, String name, double concentration, String concentrationUnit, String chemicalClass) {

		IPeakModel peakModel = peak.getPeakModel();
		IScan scan = peakModel.getPeakMaximum();
		int retentionTime = scan.getRetentionTime();
		float retentionIndex = scan.getRetentionIndex();
		//
		IQuantitationCompound quantitationCompound = new QuantitationCompound(name, concentrationUnit, retentionTime);
		quantitationCompound.setChemicalClass(chemicalClass);
		//
		IRetentionTimeWindow retentionTimeWindow = quantitationCompound.getRetentionTimeWindow();
		if(retentionTime > 0) {
			retentionTimeWindow.setAllowedNegativeDeviation((int)(PreferenceSupplier.getRetentionTimeNegativeDeviation() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			retentionTimeWindow.setAllowedPositiveDeviation((int)(PreferenceSupplier.getRetentionTimePositiveDeviation() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
		}
		//
		IRetentionIndexWindow retentionIndexWindow = quantitationCompound.getRetentionIndexWindow();
		retentionIndexWindow.setRetentionIndex(retentionIndex);
		if(retentionIndex > 0) {
			retentionIndexWindow.setAllowedNegativeDeviation(PreferenceSupplier.getRetentionIndexNegativeDeviation());
			retentionIndexWindow.setAllowedPositiveDeviation(PreferenceSupplier.getRetentionIndexPositiveDeviation());
		}
		//
		IQuantitationPeak quantitationPeakMSD = new QuantitationPeakMSD((IPeakMSD)peak, concentration, concentrationUnit);
		quantitationCompound.getQuantitationPeaks().add(quantitationPeakMSD);
		//
		quantitationCompound.setQuantitationSignalTIC();
		//
		return quantitationCompound;
	}

	public void merge(IQuantitationDatabase quantitationDatabase, IPeak peak, String name, double concentration) {

		IQuantitationCompound quantitationCompound = quantitationDatabase.getQuantitationCompound(name);
		if(quantitationCompound != null) {
			IQuantitationPeak quantitationPeakMSD = new QuantitationPeakMSD((IPeakMSD)peak, concentration, quantitationCompound.getConcentrationUnit());
			quantitationCompound.getQuantitationPeaks().add(quantitationPeakMSD);
		}
	}
}
