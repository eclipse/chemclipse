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
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings.ISupplierFilterSettings;
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
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilter {

	// TODO IProgressMonitor
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Try to normalize the chromatogram selection.
		 */
		float multiplier = getFilterSettings(chromatogramFilterSettings);
		IChromatogramFilterResult chromatogramFilterResult;
		try {
			applyMultiplierFilter(chromatogramSelection, multiplier);
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully normalized.");
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setProcessingResult(chromatogramFilterResult);
		return processingInfo;
	}

	// TODO Junit
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	// ----------------------------private methods
	private float getFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		double multiplier = 1;
		double divisor = 1;
		/*
		 * Get the excluded ions instance.
		 */
		if(chromatogramFilterSettings instanceof ISupplierFilterSettings) {
			ISupplierFilterSettings settings = (ISupplierFilterSettings)chromatogramFilterSettings;
			multiplier = settings.getMultiplier();
			divisor = settings.getDivisor();
		} else {
			ISupplierFilterSettings filterSettings = PreferenceSupplier.getChromatogramFilterSettings();
			multiplier = filterSettings.getMultiplier();
			divisor = filterSettings.getDivisor();
		}
		return (float)(multiplier / divisor);
	}

	private void applyMultiplierFilter(IChromatogramSelection chromatogramSelection, float multiplier) throws FilterException {

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
			TotalScanSignalsModifier.multiple(totalScanSignals, multiplier);
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
