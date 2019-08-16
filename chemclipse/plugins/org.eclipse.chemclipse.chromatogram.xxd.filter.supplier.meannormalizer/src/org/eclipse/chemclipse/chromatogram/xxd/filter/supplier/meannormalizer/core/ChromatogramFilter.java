/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.meannormalizer.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.meannormalizer.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.meannormalizer.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.meannormalizer.settings.FilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.CalculationException;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.NoTotalSignalStoredException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilter extends AbstractChromatogramFilter {

	// TODO IProgressMonitor
	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettings) {
				try {
					applyMeanNormalizerFilter(chromatogramSelection);
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully normalized."));
				} catch(FilterException e) {
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
				}
			}
		}
		return processingInfo;
	}

	// TODO Junit
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettings filterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	@SuppressWarnings("unchecked")
	private void applyMeanNormalizerFilter(IChromatogramSelection chromatogramSelection) throws FilterException {

		/*
		 * Get the chromatogram to determine the start and stop scan of the
		 * chromatogram selection.
		 */
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * Get the total ion signals.
		 */
		ITotalScanSignals totalScanSignals;
		try {
			/*
			 * Try to mean normalize the total ion signals. The scans of the
			 * chromatogram are still untouched, the total ion signals instance
			 * is just a copy of the scan values. Good luck, i still implemented
			 * a mean normalization method in the helper class
			 * "TotalIonSignalsModifier.java".
			 */
			ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			totalScanSignals = totalScanSignalExtractor.getTotalScanSignals(startScan, stopScan);
			TotalScanSignalsModifier.meanNormalize(totalScanSignals);
		} catch(NoTotalSignalStoredException e) {
			throw new FilterException("There were no total ion signals stored.");
		} catch(CalculationException e) {
			throw new FilterException(e.getMessage());
		} catch(ChromatogramIsNullException e) {
			throw new FilterException("The chromatogram must be not null.");
		}
		float normalizedAbundance;
		/*
		 * Iterate through all selected scans and set the calculated abundance
		 * values.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan scanRecord = chromatogram.getScan(scan);
			/*
			 * Get the normalized abundance and correct it, if necessary. Take
			 * care, some supplier e.g. Agilent are not able to save all kind of
			 * ion and abundance values due to their data format. But in most
			 * cases, problems will occur if the values are too high.
			 */
			normalizedAbundance = totalScanSignals.getTotalScanSignal(scan).getTotalSignal();
			if(normalizedAbundance <= 0.0f) {
				normalizedAbundance = 0.1f;
			}
			scanRecord.adjustTotalSignal(normalizedAbundance);
		}
	}
	// ----------------------------private methods
}
