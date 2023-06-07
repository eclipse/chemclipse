/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - Ion-wise msd chromatogram filter
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.core.runtime.IProgressMonitor;

public class SavitzkyGolayProcessor {

	public static IChromatogramFilterResult smooth(IChromatogramSelection<?, ?> chromatogramSelection, boolean validatePositive, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		IChromatogramFilterResult chromatogramFilterResult;
		try {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			TotalScanSignalExtractor signalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals totalScanSignals = signalExtractor.getTotalScanSignals(chromatogramSelection, validatePositive);
			//
			double[] sgTic = smooth(totalScanSignals, filterSettings, monitor);
			int i = 0;
			for(ITotalScanSignal signal : totalScanSignals.getTotalScanSignals()) {
				signal.setTotalSignal((float)sgTic[i++]);
			}
			/*
			 * Apply the smoothed data to the chromatogram selection.
			 */
			int startScan = totalScanSignals.getStartScan();
			int stopScan = totalScanSignals.getStopScan();
			//
			for(int scan = startScan; scan <= stopScan; scan++) {
				monitor.subTask("Set Savitzky-Golay TIC: " + scan);
				IScan scanRecord = chromatogram.getScan(scan);
				/*
				 * A value of 0 is not allowed in case of MSD data.
				 * If it's FID data, it's possible to have negative values.
				 */
				float intensity = totalScanSignals.getTotalScanSignal(scan).getTotalSignal();
				if(validatePositive && intensity <= 0.0f) {
					intensity = 0.1f;
				}
				scanRecord.adjustTotalSignal(intensity);
			}
			//
			chromatogram.setDirty(true);
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been applied successfully.");
		} catch(ChromatogramIsNullException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, "Something has gone wrong to apply the Savitzky-Golay filter.");
		}
		return chromatogramFilterResult;
	}

	public static double[] smooth(double[] ticValues, SavitzkyGolayFilter filter, IProgressMonitor monitor) {

		return smoothValues(ticValues, filter);
	}

	public static double[] smooth(double[] ticValues, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(filterSettings.getOrder(), filterSettings.getWidth(), filterSettings.getDerivative());
		return smoothValues(ticValues, filter);
	}

	public static double[] smooth(double[] ticValues, int derivative, int order, int width, IProgressMonitor monitor) {

		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(order, width, derivative);
		return smoothValues(ticValues, filter);
	}

	public static double[] smooth(ITotalScanSignals totalScanSignals, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		int size = totalScanSignals.size();
		double[] ticValues = new double[size];
		int column = 0;
		for(ITotalScanSignal signal : totalScanSignals.getTotalScanSignals()) {
			ticValues[column++] = signal.getTotalSignal();
		}
		//
		int derivative = filterSettings.getDerivative();
		int order = filterSettings.getOrder();
		int width = filterSettings.getWidth();
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(order, width, derivative);
		return smoothValues(ticValues, filter);
	}

	private static double[] smoothValues(double[] ticValues, SavitzkyGolayFilter filter) {

		return filter.apply(ticValues);
	}

	public static IChromatogramFilterResult apply(ITotalScanSignals totalSignals, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		double[] sgTic = smooth(totalSignals, filterSettings, monitor);
		int i = 0;
		for(ITotalScanSignal signal : totalSignals.getTotalScanSignals()) {
			signal.setTotalSignal((float)sgTic[i++]);
		}
		return new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been applied successfully.");
	}

	public static void apply(double[][] matrix, ChromatogramFilterSettings filterSettings) {

		double[] ionSignal = new double[matrix.length];
		int derivative = filterSettings.getDerivative();
		int order = filterSettings.getOrder();
		int width = filterSettings.getWidth();
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(order, width, derivative);
		for(int i = 0; i < matrix[0].length; i++) {
			for(int j = 0; j < matrix.length; j++) {
				ionSignal[j] = matrix[j][i];
			}
			ionSignal = smoothValues(ionSignal, filter);
			for(int j = 0; j < matrix.length; j++) {
				if(ionSignal[j] < 0.0) {
					ionSignal[j] = 0.0;
				}
				matrix[j][i] = ionSignal[j];
			}
		}
	}
}
