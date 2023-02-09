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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.ISegment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.SegmentAreaCalculator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.numeric.core.Point;

public class BackgroundIntegrator {

	private static final Logger logger = Logger.getLogger(BackgroundIntegrator.class);
	//
	private static final double CORRECTION_FACTOR_TRAPEZOID = 100.0d; // ChemStation Factor

	public double integrate(IChromatogramSelection<?, ?> chromatogramSelection) {

		double backgroundArea = 0.0d;
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
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

	/**
	 * Calculate the area of the peak in the given retention time
	 * segment.
	 */
	private double calculateArea(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		// PeakSignalPoint
		Point psp1 = new Point(startRetentionTime, startAbundance);
		Point psp2 = new Point(stopRetentionTime, stopAbundance);
		// PeakSignalPoints
		Point pbp1 = new Point(startRetentionTime, 0.0d);
		Point pbp2 = new Point(stopRetentionTime, 0.0d);
		ISegment segment = new Segment(pbp1, pbp2, psp1, psp2);
		return SegmentAreaCalculator.calculateSegmentArea(segment) / CORRECTION_FACTOR_TRAPEZOID;
	}
}
