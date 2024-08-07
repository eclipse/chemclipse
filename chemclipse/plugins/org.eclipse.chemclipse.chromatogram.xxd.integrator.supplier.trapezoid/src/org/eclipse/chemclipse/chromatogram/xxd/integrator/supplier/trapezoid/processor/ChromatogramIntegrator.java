/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;

public class ChromatogramIntegrator extends AbstractIntegrator {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegrator.class);

	public double integrate(IChromatogramSelection<?, ?> chromatogramSelection) {

		double chromatogramArea = 0.0d;
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		//
		try {
			ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			ITotalScanSignals totalIonSignals = totalScanSignalExtractor.getTotalScanSignals(startScan, stopScan);
			ITotalScanSignal startSignal;
			ITotalScanSignal stopSignal;
			/*
			 * Calculates the area for each segment.
			 */
			for(int scan = startScan; scan < stopScan; scan++) {
				startSignal = totalIonSignals.getTotalScanSignal(scan);
				stopSignal = totalIonSignals.getTotalScanSignal(scan + 1);
				if(startSignal != null && stopSignal != null) {
					double segmentArea = calculateArea(startSignal.getRetentionTime(), stopSignal.getRetentionTime(), startSignal.getTotalSignal(), stopSignal.getTotalSignal());
					chromatogramArea += segmentArea;
				}
			}
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		return chromatogramArea;
	}
}