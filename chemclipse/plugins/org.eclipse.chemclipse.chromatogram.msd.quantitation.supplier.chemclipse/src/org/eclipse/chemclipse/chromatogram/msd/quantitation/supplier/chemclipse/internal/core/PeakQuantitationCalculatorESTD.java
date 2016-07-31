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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.PeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.QuantitationCalculatorMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakQuantitationCalculatorESTD {

	private static final Logger logger = Logger.getLogger(PeakQuantitationCalculatorESTD.class);

	/**
	 * Calculates the quantitation results for each peak.
	 * 
	 * @param peaks
	 * @param peakQuantifierSettings
	 * @param monitor
	 * @return
	 */
	public IPeakQuantifierProcessingInfo quantify(List<IPeakMSD> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		IPeakQuantifierProcessingInfo processingInfo = new PeakQuantifierProcessingInfo();
		IQuantDatabase database;
		try {
			database = QuantDatabases.getQuantDatabase();
			//
			QuantitationCalculatorMSD calculator = new QuantitationCalculatorMSD();
			List<IQuantitationCompoundMSD> quantitationCompounds = database.getQuantitationCompounds();
			//
			for(IPeakMSD peakMSD : peaks) {
				/*
				 * Try to quantify the selected peak.
				 * The results will be added to the peak.
				 */
				List<IQuantitationCompoundMSD> quantitationCompoundsMSD = getQuantitationEntries(quantitationCompounds, peakMSD);
				List<IQuantitationEntryMSD> entries = calculator.calculateQuantitationResults(peakMSD, quantitationCompoundsMSD, processingInfo);
				for(IQuantitationEntryMSD quantitationEntry : entries) {
					peakMSD.addQuantitationEntry(quantitationEntry);
				}
			}
		} catch(NoQuantitationTableAvailableException e) {
			processingInfo.addErrorMessage("ChemClipse Quantitation", "Please select a quantitation table.");
			logger.warn(e);
		}
		return processingInfo;
	}

	private List<IQuantitationCompoundMSD> getQuantitationEntries(List<IQuantitationCompoundMSD> quantitationCompounds, IPeakMSD peakMSD) {

		List<IQuantitationCompoundMSD> quantitationCompoundsMSD = new ArrayList<IQuantitationCompoundMSD>();
		for(IQuantitationCompoundMSD quantitationCompound : quantitationCompounds) {
			/*
			 * Add the compound if it matches certain conditions:
			 * Retention Time Window
			 */
			int retentionTime = peakMSD.getPeakModel().getRetentionTimeAtPeakMaximum();
			IRetentionTimeWindow retentionTimeWindow = quantitationCompound.getRetentionTimeWindow();
			if(retentionTimeWindow.isRetentionTimeInWindow(retentionTime)) {
				quantitationCompoundsMSD.add(quantitationCompound);
			}
		}
		return quantitationCompoundsMSD;
	}
}
