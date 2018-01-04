/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.unitsumnormalizer.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.unitsumnormalizer.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.unitsumnormalizer.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.NoTotalSignalStoredException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilter {

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		/* Check whether selection is fine */
		IChromatogramFilterProcessingInfo processingInfo = new ChromatogramFilterProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		// TODO set filter settings?
		IChromatogramFilterResult chromatogramFilterResult;
		try {
			applyUnitSumNormalizerFilter(chromatogramSelection);
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully normalized.");
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setChromatogramFilterResult(chromatogramFilterResult);
		return processingInfo;
	}

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	// ----------------------------private methods
	private void applyUnitSumNormalizerFilter(IChromatogramSelection chromatogramSelection) throws FilterException {

		/*
		 * Get the chromatogram to determine the start and stop scan of the
		 * chromatogram selection.
		 */
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * Calculation of area sum intensity
		 */
		double areaSumIntensity;
		/*
		 * Getting multiplicationFactor from preferences
		 */
		areaSumIntensity = chromatogram.getTotalSignal();
		/*
		 * Get the total ion signals.
		 */
		ITotalScanSignals totalScanSignals;
		/*
		 * Try to mean normalize the total ion signals. The scans of the
		 * chromatogram are still untouched, the total ion signals instance
		 * is just a copy of the scan values. Good luck, i still implemented
		 * a mean normalization method in the helper class
		 * "TotalIonSignalsModifier.java".
		 */
		ITotalScanSignalExtractor totalScanSignalExtractor;
		try {
			totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			totalScanSignals = totalScanSignalExtractor.getTotalScanSignals(startScan, stopScan);
			/*
			 * End of calculation
			 */
			TotalScanSignalsModifier.unitSumNormalize(totalScanSignals, areaSumIntensity);
		} catch(ChromatogramIsNullException e) {
			throw new FilterException("The chromatogram must be not null.");
		} catch(NoTotalSignalStoredException e) {
			throw new FilterException("There were no total ion signals stored.");
		}
		/*
		 * Iterate through all selected scans and set the calculated abundance
		 * values.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan scanRecord = chromatogram.getScan(scan);
			float totalSignal = totalScanSignals.getTotalScanSignal(scan).getTotalSignal();
			scanRecord.adjustTotalSignal(totalSignal);
		}
	}
}
