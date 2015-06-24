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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.PeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.QuantitationCalculatorMSD;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;

public class PeakQuantitationCalculator {

	private static final Logger logger = Logger.getLogger(PeakQuantitationCalculator.class);

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
			IQuantDatabases databases = new QuantDatabases();
			database = databases.getQuantDatabase();
			//
			QuantitationCalculatorMSD calculator = new QuantitationCalculatorMSD();
			List<IQuantitationCompoundDocument> quantitationCompoundDocuments = database.getQuantitationCompoundDocuments();
			//
			for(IPeakMSD peakMSD : peaks) {
				/*
				 * Try to quantify the selected peak.
				 * The results will be added to the peak.
				 */
				List<IQuantitationCompoundMSD> quantitationCompoundsMSD = getQuantitationEntries(quantitationCompoundDocuments, peakMSD);
				List<IQuantitationEntryMSD> entries = calculator.calculateQuantitationResults(peakMSD, quantitationCompoundsMSD, processingInfo);
				for(IQuantitationEntryMSD quantitationEntry : entries) {
					peakMSD.addQuantitationEntry(quantitationEntry);
				}
			}
		} catch(NoDatabaseAvailableException e) {
			processingInfo.addErrorMessage("ChemClipse Quantitation", "Please select a quantitation table.");
			logger.warn(e);
		}
		return processingInfo;
	}

	private List<IQuantitationCompoundMSD> getQuantitationEntries(List<IQuantitationCompoundDocument> quantitationCompoundDocuments, IPeakMSD peakMSD) {

		List<IQuantitationCompoundMSD> quantitationCompoundsMSD = new ArrayList<IQuantitationCompoundMSD>();
		for(IQuantitationCompoundDocument quantitationCompoundDocument : quantitationCompoundDocuments) {
			/*
			 * Add the compound if it matches certain conditions:
			 * Retention Time Window
			 */
			int retentionTime = peakMSD.getPeakModel().getRetentionTimeAtPeakMaximum();
			if(retentionTime >= quantitationCompoundDocument.getLowerRetentionTimeLimit() && retentionTime <= quantitationCompoundDocument.getUpperRetentionTimeLimit()) {
				quantitationCompoundsMSD.add(quantitationCompoundDocument.getQuantitationCompound());
			}
		}
		return quantitationCompoundsMSD;
	}
}
