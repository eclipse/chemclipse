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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator.QuantitationCalculatorMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.model.quantitation.QuantitationDatabase;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakQuantitationCalculatorESTD extends AbstractPeakQuantitationCalculator {

	private static final Logger logger = Logger.getLogger(PeakQuantitationCalculatorESTD.class);

	/**
	 * Calculates the quantitation results for each peak.
	 * 
	 * @param peaks
	 * @param peakQuantifierSettings
	 * @param monitor
	 * @return
	 */
	public IProcessingInfo quantify(List<IPeak> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		System.out.println("Load Selected QuantDB");
		IQuantitationDatabase quantitationDatabase = new QuantitationDatabase(); // TODO Load
		if(quantitationDatabase != null && quantitationDatabase.size() > 0) {
			QuantitationCalculatorMSD calculator = new QuantitationCalculatorMSD();
			for(IPeak peakMSD : peaks) {
				/*
				 * Try to quantify the selected peak.
				 * The results will be added to the peak.
				 */
				Set<IQuantitationCompound> quantitationCompoundsMSD = getQuantitationEntries(quantitationDatabase, peakMSD);
				List<IQuantitationEntryMSD> entries = calculator.calculateQuantitationResults(peakMSD, quantitationCompoundsMSD, processingInfo);
				for(IQuantitationEntryMSD quantitationEntry : entries) {
					peakMSD.addQuantitationEntry(quantitationEntry);
				}
			}
		} else {
			processingInfo.addErrorMessage("ChemClipse Quantitation", "Please select a quantitation table.");
		}
		return processingInfo;
	}

	private Set<IQuantitationCompound> getQuantitationEntries(Set<IQuantitationCompound> quantitationCompounds, IPeak peakToQuantify) {

		Set<IQuantitationCompound> quantitationCompoundsMSD = new HashSet<IQuantitationCompound>();
		for(IQuantitationCompound quantitationCompound : quantitationCompounds) {
			/*
			 * Add the compound if it matches certain conditions:
			 * Retention Time Window
			 */
			int retentionTime = peakToQuantify.getPeakModel().getRetentionTimeAtPeakMaximum();
			IRetentionTimeWindow retentionTimeWindow = quantitationCompound.getRetentionTimeWindow();
			if(retentionTimeWindow.isRetentionTimeInWindow(retentionTime)) {
				if(doQuantify(peakToQuantify, quantitationCompound.getName())) {
					quantitationCompoundsMSD.add(quantitationCompound);
				}
			}
		}
		return quantitationCompoundsMSD;
	}
}
