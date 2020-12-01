/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.internal.core;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

public class ChromatogramIntegrator extends AbstractSumareaIntegrator implements ISumareaIntegrator {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegrator.class);

	@Override
	public double integrate(IChromatogramSelectionMSD chromatogramSelection) {

		return integrate(chromatogramSelection, (int)AbstractIon.TIC_ION);
	}

	@Override
	public double integrate(IChromatogramSelectionMSD chromatogramSelection, int ion) {

		double chromatogramArea = 0.0d;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		/*
		 * Try to integrate the signals.
		 */
		try {
			IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			IExtractedIonSignal startSignal;
			IExtractedIonSignal stopSignal;
			double segmentArea = 0.0d;
			/*
			 * Calculates the area for each segment.
			 */
			for(int scan = startScan; scan < stopScan; scan++) {
				try {
					startSignal = extractedIonSignals.getExtractedIonSignal(scan);
					stopSignal = extractedIonSignals.getExtractedIonSignal(scan + 1);
					if(startSignal != null && stopSignal != null) {
						float startAbundance = startSignal.getAbundance(ion);
						float stopAbundance = stopSignal.getAbundance(ion);
						segmentArea = calculateArea(startSignal.getRetentionTime(), stopSignal.getRetentionTime(), startAbundance, stopAbundance);
						chromatogramArea += segmentArea;
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		} catch(ChromatogramIsNullException e1) {
			logger.warn(e1);
		}
		return chromatogramArea;
	}
}
