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
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;

public class BackgroundIntegrator extends AbstractIntegrator {

	private static final Logger logger = Logger.getLogger(BackgroundIntegrator.class);

	public double integrate(IChromatogramSelection<?, ?> chromatogramSelection) {

		double backgroundArea = 0.0d;
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		//
		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals totalIonSignals = totalIonSignalExtractor.getTotalScanSignals();
			/*
			 * Calculates the area for each background element.
			 */
			for(int scan = startScan; scan < stopScan; scan++) {
				ITotalScanSignal startSignal = totalIonSignals.getTotalScanSignal(scan);
				ITotalScanSignal stopSignal = totalIonSignals.getTotalScanSignal(scan + 1);
				if(startSignal != null && stopSignal != null) {
					int start = startSignal.getRetentionTime();
					int stop = stopSignal.getRetentionTime();
					double segmentArea = calculateArea(start, stop, baselineModel.getBackgroundAbundance(start), baselineModel.getBackgroundAbundance(stop));
					backgroundArea += segmentArea;
				}
			}
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		return backgroundArea;
	}
}