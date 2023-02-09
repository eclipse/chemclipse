/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.SegmentAreaCalculator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.numeric.core.Point;

public class ChromatogramIntegrator {

	private static final Logger logger = Logger.getLogger(ChromatogramIntegrator.class);
	//
	private static final double CORRECTION_FACTOR_TRAPEZOID = 100.0d; // ChemStation Factor

	public double integrate(IChromatogramSelection<?, ?> chromatogramSelection) {

		double chromatogramArea = 0.0d;
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
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

	/**
	 * Calculate the area of the peak in the given retention time
	 * segment assume the baseline is 0.
	 */
	private double calculateArea(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		// PeakSignalPoint
		Point psp1 = new Point(startRetentionTime, startAbundance);
		Point psp2 = new Point(stopRetentionTime, stopAbundance);
		// PeakBaselinePoint
		Point pbp1 = new Point(startRetentionTime, 0.0d);
		Point pbp2 = new Point(stopRetentionTime, 0.0d);
		Segment segment = new Segment(pbp1, pbp2, psp1, psp2);
		return SegmentAreaCalculator.calculateSegmentArea(segment) / CORRECTION_FACTOR_TRAPEZOID;
	}
}
