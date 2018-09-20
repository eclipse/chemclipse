/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.detector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.BackfoldingPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.FilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.IBackfoldingPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.Threshold;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.support.IBackfoldingDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ExtendedTotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.core.runtime.IProgressMonitor;

// TODO JUnit
/**
 * This class implements the backfolding detection algorithm.<br/>
 * See:<br/>
 * "Differential Gas-Chromatographic Mass-Spectrometry", GHOSH, A. and ANDEREGG,
 * R. J., 1989<br/>
 * "Backfolding applied to differential gas chromatography mass spectrometry as a mathematical enhancement of chromatographic resolution"
 * , Pool, W. G. and deLeeuw, J. W. and van de Graaf, B., 1996<br/>
 * "Automated extraction of pure mass spectra from gas chromatographic mass spectrometric data"
 * , Pool, W. G. and deLeeuw, J. W. and van de Graaf, B., 1997<br/>
 * "Automated processing of GC/MS data: Quantification of the signals of individual components"
 * , Pool, W. G. and deLeeuw, J. W. and van de Graaf, B., 1997<br/>
 * 
 * @author eselmeister
 */
public class BackfoldingShifter implements IBackfoldingShifter {

	private static final Logger logger = Logger.getLogger(BackfoldingShifter.class);

	// ---------------------------------------------------IBackfoldingShifter
	@Override
	public IExtractedIonSignals shiftIons(IChromatogramSelectionMSD chromatogramSelection, FilterSettings filterSettings, IProgressMonitor monitor) {

		/*
		 * Get the extracted ion signals in the range of the chromatogram
		 * selection.
		 */
		IExtractedIonSignals extractedIonSignalsShifted = null;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		try {
			IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			extractedIonSignalsShifted = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			/*
			 * There must be at least 1 backfolding run. Run the backfolding the
			 * number of times given in getNumberOfBackfoldingRuns().
			 */
			int runs = filterSettings.getNumberOfBackfoldingRuns();
			for(int run = 1; run <= runs; run++) {
				String taskDescription = "Shift scans run (" + run + "/" + runs + ")";
				monitor.subTask(taskDescription);
				extractedIonSignalsShifted = calculateExtractedIonSignals(extractedIonSignalsShifted, taskDescription, filterSettings, monitor);
			}
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		return extractedIonSignalsShifted;
	}

	// ---------------------------------------------------IBackfoldingShifter
	// ---------------------------------------------------private methods
	/**
	 * Returns an shifted extracted ion signals instance.<br/>
	 * The
	 * 
	 * @param IExtractedIonSignals
	 * @return {@link IExtractedIonSignals}
	 */
	private IExtractedIonSignals calculateExtractedIonSignals(IExtractedIonSignals extractedIonSignals, String taskDescription, FilterSettings filterSettings, IProgressMonitor monitor) {

		ITotalScanSignals totalIonSignals;
		/*
		 * Make a deep copy of the extracted ion signals, but without mass
		 * fragments stored.<br/> Only the scan information is available.<br/>
		 * Why not do it with the given extracted ion signals instance?<br/>
		 * Because we need the original data of all ions that should
		 * be shifted.
		 */
		IExtractedIonSignals extractedIonSignalsShifted = extractedIonSignals.makeDeepCopyWithoutSignals();
		/*
		 * Use the full ion range.
		 */
		int startIon = extractedIonSignals.getStartIon();
		int stopIon = extractedIonSignals.getStopIon();
		/*
		 * Process each ion.
		 */
		for(int ion = startIon; ion <= stopIon; ion++) {
			monitor.subTask(taskDescription + " - ion: " + ion);
			totalIonSignals = extractedIonSignals.getTotalIonSignals(ion);
			adjustIon(ion, totalIonSignals, extractedIonSignalsShifted, filterSettings);
		}
		return extractedIonSignalsShifted;
	}

	/**
	 * Adjusts the given ion and stores the corrected retention times in
	 * extractedIonSignalsShifted.<br/>
	 * The signals of the given ion are stored in totalIonSignals.
	 * 
	 * @param ion
	 * @param totalIonSignals
	 * @param extractedIonSignalsShifted
	 */
	private void adjustIon(int ion, ITotalScanSignals totalIonSignals, IExtractedIonSignals extractedIonSignalsShifted, FilterSettings filterSettings) {

		int startScan = extractedIonSignalsShifted.getStartScan();
		int stopScan = extractedIonSignalsShifted.getStopScan();
		IChromatogramMSD chromatogram = extractedIonSignalsShifted.getChromatogram();
		ITotalScanSignals totalIonSignalsShifted = calculateTotalIonSignalsShifted(totalIonSignals, chromatogram, startScan, stopScan);
		addShiftedIonSignalsToExtractedIonSignals(ion, totalIonSignalsShifted, extractedIonSignalsShifted, filterSettings);
	}

	/**
	 * Calculates the delta abundance of each scan and returns a new instance of {@link ITotalScanSignals}.<br/>
	 * The returned {@link ITotalScanSignals} instance stores instances of {@link IBackfoldingTotalIonSignal}<br/>
	 * All instances of IBackfoldingTotalIonSignal allow also negative
	 * abundances.
	 * 
	 * @param totalIonSignals
	 * @param chromatogram
	 * @param startScan
	 * @param stopScan
	 * @return {@link ITotalScanSignals}
	 */
	private ITotalScanSignals calculateTotalIonSignalsShifted(ITotalScanSignals totalIonSignals, IChromatogramMSD chromatogram, int startScan, int stopScan) {

		ITotalScanSignal totalIonSignal;
		float actualSignal;
		float nextSignal;
		float deltaSignal;
		/*
		 * Create a new instance of ITotalIonSignals.<br/> All instances of
		 * IBackfoldingTotalIonSignal allow also negative abundances.
		 */
		ITotalScanSignals totalIonSignalsShifted = new TotalScanSignals(startScan, stopScan, chromatogram);
		ITotalScanSignal totalIonSignalShifted;
		/*
		 * Iterate through all scans, subtract the actual from the next signal
		 * and store it in a new total ion signals object (which allows negative
		 * total signals).
		 */
		for(int scan = startScan; scan < stopScan; scan++) {
			/*
			 * Get the signals.
			 */
			totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			actualSignal = totalIonSignal.getTotalSignal();
			nextSignal = totalIonSignals.getNextTotalScanSignal(scan).getTotalSignal();
			/*
			 * Calculate the delta signal, create a new
			 * IBackfoldingTotalIonSignal instance and store it in the fresh
			 * ITotalIonSignals instance.
			 */
			deltaSignal = nextSignal - actualSignal;
			totalIonSignalShifted = new ExtendedTotalScanSignal(totalIonSignal.getRetentionTime(), totalIonSignal.getRetentionIndex(), deltaSignal);
			totalIonSignalsShifted.add(totalIonSignalShifted);
		}
		/*
		 * The last scan can't be subtracted, so add it naturally to the shifted
		 * total ion signals.<br/> If there is one element less in the deep
		 * copied ion signals instance, there would occur a NullPointerException
		 * in BackfoldingPeakDetectorSupport (getBackfoldingSlopes).
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(stopScan);
		actualSignal = totalIonSignal.getTotalSignal();
		totalIonSignalShifted = new ExtendedTotalScanSignal(totalIonSignal.getRetentionTime(), totalIonSignal.getRetentionIndex(), actualSignal);
		totalIonSignalsShifted.add(totalIonSignalShifted);
		return totalIonSignalsShifted;
	}

	/**
	 * Adds all abundances values (ITotalIonSignals) from the given ion to the
	 * IExtractedIonSignals instance.<br/>
	 * For each retention time the nearest scan will be retrieved and the
	 * abundance will be added to the scan, denoted by the given ion.<br/>
	 * All values will be stored as absolute values (negative will become
	 * positive). Several abundance values for the same scan will be added.
	 * 
	 * @param ion
	 * @param totalIonSignalsShifted
	 * @param extractedIonSignalsShifted
	 */
	private void addShiftedIonSignalsToExtractedIonSignals(int ion, ITotalScanSignals totalIonSignalsShifted, IExtractedIonSignals extractedIonSignalsShifted, FilterSettings filterSettings) {

		ITotalScanSignal totalIonSignal;
		/*
		 * Shift all positive total ion signal by the delta retention time shift
		 * to the right and all negative values to the left. The
		 * extractedIonSignalsShifted will only store absolute positive values.
		 */
		int retentionTime = 0;
		float abundance = 0.0f;
		/*
		 * Select the start and stop scan.
		 */
		int startScan = extractedIonSignalsShifted.getStartScan();
		int stopScan = extractedIonSignalsShifted.getStopScan();
		/*
		 * Calculate the retention time to which the scans should be
		 * shifted.<br/> Negative values will be shifted backwards, positive
		 * values will be shifted forwards.
		 */
		int deltaRetentionTime = calculateDeltaRetentionTimeShift(totalIonSignalsShifted, filterSettings);
		for(int scan = startScan; scan < stopScan; scan++) {
			totalIonSignal = totalIonSignalsShifted.getTotalScanSignal(scan);
			/*
			 * If the total signal is positive, shift the retention time
			 * right.<br/> If the total signal is negative, shift the retention
			 * time left.
			 */
			retentionTime = totalIonSignal.getRetentionTime();
			if(totalIonSignal.getTotalSignal() >= 0) {
				retentionTime += deltaRetentionTime;
			} else {
				retentionTime -= deltaRetentionTime;
			}
			/*
			 * Add the absolute signal and do not remove previous signals but
			 * merge them.
			 */
			abundance = Math.abs(totalIonSignal.getTotalSignal());
			extractedIonSignalsShifted.add(ion, abundance, retentionTime, false);
		}
	}

	/**
	 * Calculates how much the retention should be shifted for the given {@link ITotalScanSignals}.
	 * 
	 * @param totalIonSignalsShifted
	 * @return int
	 */
	private int calculateDeltaRetentionTimeShift(ITotalScanSignals totalIonSignalsShifted, FilterSettings filterSettings) {

		ITotalScanSignals totalIonSignalsNegative = getTotalIonSignalsNegative(totalIonSignalsShifted);
		ITotalScanSignals totalIonSignalsPositive = getTotalIonSignalsPositive(totalIonSignalsShifted);
		/*
		 * Calculate the detected raw peaks and calculate a delta retention time
		 * as a parameter to shift the ion values.<br/> Use per
		 * default the given peak detector settings.<br/> Should the operator be
		 * able to use another settings?<br/> I my opinion, i think no.
		 * eselmeister<br/> But if it will be needed, it could be implemented.
		 */
		IBackfoldingPeakDetectorSettings peakDetectorSettings = new BackfoldingPeakDetectorSettings();
		peakDetectorSettings.setThreshold(Threshold.OFF);
		List<IRawPeak> rawPeaksNegative = getRawPeaks(totalIonSignalsNegative, peakDetectorSettings);
		List<IRawPeak> rawPeaksPositive = getRawPeaks(totalIonSignalsPositive, peakDetectorSettings);
		/*
		 * Use the peak list with the lower amount of peaks to not run into an
		 * array out of bounds exception.<br/> Return 0 if there are no peaks.
		 */
		int peaks = Math.min(rawPeaksNegative.size(), rawPeaksPositive.size());
		if(peaks == 0) {
			return 0;
		}
		/*
		 * Use a SortedMap (TreeMap) to retrieve correlated peaks, because the
		 * amount of detected peaks could differ between positive and negative
		 * total ion signals.<br/> key - scan number value - retention time (in
		 * milliseconds)
		 */
		NavigableMap<Integer, Integer> positiveTreeMap = getTreeMap(rawPeaksPositive);
		/*
		 * There shall be no value 0, so that median could be used.
		 */
		int[] deltaPeakDistances = calculateDeltaPeakDistances(peaks, rawPeaksNegative, positiveTreeMap, filterSettings);
		int result = Calculations.getMedian(deltaPeakDistances);
		return result / 2;
	}

	/**
	 * Make a deep copy of totalIonSignalsShifted.<br/>
	 * Set positive total signals to 0 and negative values as absolute values.
	 * 
	 * @param totalIonSignalsShifted
	 * @return {@link ITotalScanSignals}
	 */
	private ITotalScanSignals getTotalIonSignalsNegative(ITotalScanSignals totalIonSignalsShifted) {

		ITotalScanSignals totalIonSignalsNegative = totalIonSignalsShifted.makeDeepCopy();
		totalIonSignalsNegative.setPositiveTotalSignalsToZero();
		totalIonSignalsNegative.setTotalSignalsAsAbsoluteValues();
		return totalIonSignalsNegative;
	}

	/**
	 * Make a deep copy of totalIonSignalsShifted.<br/>
	 * Set negative total signals to 0.
	 * 
	 * @param totalIonSignalsShifted
	 * @return {@link ITotalScanSignals}
	 */
	private ITotalScanSignals getTotalIonSignalsPositive(ITotalScanSignals totalIonSignalsShifted) {

		ITotalScanSignals totalIonSignalsPositive = totalIonSignalsShifted.makeDeepCopy();
		totalIonSignalsPositive.setNegativeTotalSignalsToZero();
		return totalIonSignalsPositive;
	}

	/**
	 * Returns the calculated raw peaks.
	 * 
	 * @return List<IRawPeak>
	 */
	private List<IRawPeak> getRawPeaks(ITotalScanSignals totalIonSignals, IPeakDetectorMSDSettings peakDetectorSettings) {

		IBackfoldingDetectorSlopes slopes;
		slopes = BackfoldingPeakDetectorSupport.getBackfoldingSlopes(totalIonSignals, peakDetectorSettings);
		List<IRawPeak> rawPeaks = BackfoldingPeakDetectorSupport.getRawPeaks(slopes, peakDetectorSettings);
		return rawPeaks;
	}

	/**
	 * Returns a SortedMap (TreeMap) to retrieve correlated peaks, because the
	 * amount of detected peaks could differ between positive and negative total
	 * ion signals.<br/>
	 * key - scan number value - retention time (in milliseconds)
	 * 
	 * @param rawPeaks
	 * @return NavigableMap<Integer, Integer>
	 */
	private NavigableMap<Integer, Integer> getTreeMap(List<IRawPeak> rawPeaks) {

		NavigableMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>();
		for(IRawPeak rawPeak : rawPeaks) {
			treeMap.put(rawPeak.getMaximumScan(), rawPeak.getRetentionTimeAtMaximum());
		}
		return treeMap;
	}

	/**
	 * Calculates the delta peak distance retention times.
	 * 
	 * @param peaks
	 * @param rawPeaksNegative
	 * @param positiveTreeMap
	 * @return int[]
	 */
	private int[] calculateDeltaPeakDistances(int peaks, List<IRawPeak> rawPeaksNegative, NavigableMap<Integer, Integer> positiveTreeMap, FilterSettings filterSettings) {

		IRawPeak negativeRawPeak;
		int deltaDistance = 0;
		int negativeMaximum = 0;
		int positiveMaximum = 0;
		List<Integer> peakDistances = new ArrayList<Integer>();
		/*
		 * Divide the result by 2.
		 */
		int maxDistance = filterSettings.getMaximumRetentionTimeShift() * 2;
		/*
		 * Calculate the distance between the positive and negative peaks.
		 */
		for(int peak = 0; peak < peaks; peak++) {
			negativeRawPeak = rawPeaksNegative.get(peak);
			negativeMaximum = negativeRawPeak.getRetentionTimeAtMaximum();
			positiveMaximum = calculatePositiveMaximum(negativeRawPeak.getMaximumScan(), positiveTreeMap);
			/*
			 * The delta may not exceed a maximum difference.
			 */
			deltaDistance = Math.abs(negativeMaximum - positiveMaximum);
			if(deltaDistance <= maxDistance) {
				peakDistances.add(deltaDistance);
			}
		}
		/*
		 * Return the mean distance of the positive and negative peaks to shift
		 * the ion values.<br/>
		 */
		int[] deltaPeakDistances = new int[peakDistances.size()];
		int counter = 0;
		for(Integer value : peakDistances) {
			deltaPeakDistances[counter++] = value;
		}
		return deltaPeakDistances;
	}

	/**
	 * Returns the retention time of a positive maximum which is the next to the
	 * given scan.<br/>
	 * If there is no maximum, 0 will be returned.
	 * 
	 * @param scan
	 * @param positiveTreeMap
	 * @return int
	 */
	private int calculatePositiveMaximum(int scan, NavigableMap<Integer, Integer> positiveTreeMap) {

		int result = 0;
		/*
		 * Map.Entry<key, value><br/> key = scan<br/> value = retention time
		 */
		Map.Entry<Integer, Integer> positivePeak = positiveTreeMap.lowerEntry(scan);
		if(positivePeak != null) {
			result = positivePeak.getValue();
		}
		return result;
	}
	// ---------------------------------------------------private methods
}
