/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignals;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractBaselineSignalDetector extends AbstractBaselineDetector {

	@Override
	public IProcessingInfo<?> setBaseline(IChromatogramSelection<?, ?> chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, baselineDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			return process(chromatogramSelection, baselineDetectorSettings, monitor);
		} else {
			return processingInfo;
		}
	}

	@Override
	public IProcessingInfo<?> setBaseline(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, monitor);
		if(!processingInfo.hasErrorMessages()) {
			return process(chromatogramSelection, null, monitor);
		} else {
			return processingInfo;
		}
	}

	private IProcessingInfo<?> process(IChromatogramSelection<?, ?> chromatogramSelection, IBaselineDetectorSettings settings, IProgressMonitor monitor) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
			return process(chromatogramSelectionMSD, settings, monitor);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD chromatogramSelectionCSD) {
			return process(chromatogramSelectionCSD, settings, monitor);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
			return process(chromatogramSelectionWSD, settings, monitor);
		}
		throw new UnsupportedOperationException("Class " + chromatogramSelection.getClass().getName() + " is not supported");
	}

	private IProcessingInfo<?> process(IChromatogramSelectionCSD chromatogramSelection, IBaselineDetectorSettings settings, IProgressMonitor monitor) {

		IChromatogramCSD chromatogramCSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signal from chromatogram
		 */
		TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramCSD);
		/*
		 * here is different between CSD and MSD, in case csd is NOT check if value are negative
		 */
		ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, false);
		/*
		 * 2. step - create baseline
		 */
		IProcessingInfo<?> processInfo = baselineProcess(totalSignals, settings, monitor);
		/*
		 * 3. step - set baseline
		 */
		if(!processInfo.hasErrorMessages()) {
			applyBaseline(totalSignals, chromatogramCSD.getBaselineModel());
		}
		return processInfo;
	}

	private IProcessingInfo<?> process(IChromatogramSelectionMSD chromatogramSelection, IBaselineDetectorSettings settings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signal from chromatogram
		 */
		TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramMSD);
		/*
		 * here is different between CSD and MSD, in case msd is check if value are negative
		 */
		ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, true);
		/*
		 * 2. step - create baseline
		 */
		IProcessingInfo<?> processInfo = baselineProcess(totalSignals, settings, monitor);
		/*
		 * 3. step - set baseline
		 */
		if(!processInfo.hasErrorMessages()) {
			/*
			 * negative values to zero
			 */
			totalSignals.setNegativeTotalSignalsToZero();
			applyBaseline(totalSignals, chromatogramMSD.getBaselineModel());
		}
		return processInfo;
	}

	private IProcessingInfo<?> process(IChromatogramSelectionWSD chromatogramSelection, IBaselineDetectorSettings settings, IProgressMonitor monitor) {

		IChromatogramWSD chromatogramWSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signals from chromatogram
		 */
		IExtractedSingleWavelengthSignalExtractor extractor = new ExtractedSingleWavelengthSignalExtractor(chromatogramWSD, false);
		int startScan = chromatogramWSD.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogramWSD.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IMarkedWavelengths markedWavelenghts = chromatogramSelection.getSelectedWavelengths();
		List<IExtractedSingleWavelengthSignals> extractedSingleWavelengthSignals = extractor.getExtractedWavelengthSignals(startScan, stopScan, markedWavelenghts);
		/*
		 * 2. step - process signals - each wavelength separately
		 */
		IProcessingInfo<?> processingInfoTotal = new ProcessingInfo<>();
		for(IExtractedSingleWavelengthSignals totalSignals : extractedSingleWavelengthSignals) {
			IProcessingInfo<?> processingInfo = baselineProcess(totalSignals, settings, monitor);
			processingInfoTotal.addMessages(processingInfo);
		}
		/*
		 * 3. step - adjust signal in chromatogram if data has been process successfully
		 */
		if(!processingInfoTotal.hasErrorMessages()) {
			SortedMap<Double, SortedSet<IExtractedSingleWavelengthSignals>> sortedSignalsMap = IExtractedSingleWavelengthSignalExtractor.sortExtractedSignals(extractedSingleWavelengthSignals);
			for(Entry<Double, SortedSet<IExtractedSingleWavelengthSignals>> entry : sortedSignalsMap.entrySet()) {
				double wavelength = entry.getKey();
				IBaselineModel baselineModel = chromatogramWSD.getBaselineModel(wavelength);
				SortedSet<IExtractedSingleWavelengthSignals> signalsOnWavelength = entry.getValue();
				Iterator<IExtractedSingleWavelengthSignals> iterator = signalsOnWavelength.iterator();
				while(iterator.hasNext()) {
					IExtractedSingleWavelengthSignals signals = iterator.next();
					applyBaseline(signals, baselineModel);
				}
			}
		}
		// support not old version processing
		ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramWSD);
		ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, false);
		IProcessingInfo<?> processInfo = baselineProcess(totalSignals, settings, monitor);
		if(!processInfo.hasErrorMessages()) {
			applyBaseline(totalSignals, chromatogramWSD.getBaselineModel());
		}
		return processingInfoTotal;
	}

	private IProcessingInfo<Void> baselineProcess(ITotalScanSignals totalScanSignals, IBaselineDetectorSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		if(chromatogramFilterSettings == null) {
			return setBaseline(totalScanSignals, monitor);
		} else {
			return setBaseline(totalScanSignals, chromatogramFilterSettings, monitor);
		}
	}

	protected abstract IProcessingInfo<Void> setBaseline(ITotalScanSignals totalScanSignals, IProgressMonitor monitor);

	protected abstract IProcessingInfo<Void> setBaseline(ITotalScanSignals totalScanSignals, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor);

	private void applyBaseline(ITotalScanSignals totalIonSignals, IBaselineModel baselineModel) {

		/*
		 * remove old baseline
		 */
		ITotalScanSignal firstTotalSignal = totalIonSignals.getFirstTotalScanSignal();
		ITotalScanSignal lastTotalSignal = totalIonSignals.getLastTotalScanSignal();
		baselineModel.removeBaseline(firstTotalSignal.getRetentionTime(), lastTotalSignal.getRetentionTime());
		/*
		 * Why scan < numberOfScans instead of scan <= numberOfScans? Because of getNextTotalIonSignal()
		 */
		for(int scan = totalIonSignals.getStartScan(); scan < totalIonSignals.getStopScan(); scan++) {
			ITotalScanSignal actualTotalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			ITotalScanSignal nextTotalIonSignal = totalIonSignals.getNextTotalScanSignal(scan);
			/*
			 * Retention times and background abundances.
			 */
			int startRetentionTime = actualTotalIonSignal.getRetentionTime();
			float startBackgroundAbundance = actualTotalIonSignal.getTotalSignal();
			int stopRetentionTime = nextTotalIonSignal.getRetentionTime();
			float stopBackgroundAbundance = nextTotalIonSignal.getTotalSignal();
			/*
			 * Set the baseline.
			 * It is validate == false, cause we know that the segments are calculated without overlap.
			 */
			baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
		}
	}
}
