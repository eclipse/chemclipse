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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.core;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator.IMassChromatographicQualityResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator.MassChromatographicQualityCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.CodaCalculatorException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.FilterSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	private static WindowSize MOVING_AVERAGE_WINDOW = WindowSize.SCANS_5;

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettings) {
				try {
					FilterSettings filterSettings = (FilterSettings)chromatogramFilterSettings;
					applyCodaFilter(chromatogramSelection, filterSettings);
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection has been successfully cleaned by the coda algorithm."));
				} catch(FilterException e) {
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
				}
			}
		}
		return processingInfo;
	}

	// TODO JUnit
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	private void applyCodaFilter(IChromatogramSelectionMSD chromatogramSelection, FilterSettings filterSettings) throws FilterException {

		/*
		 * Try to get the ions, that should be excluded.
		 */
		IMarkedIons excludedIons;
		try {
			IMassChromatographicQualityResult result = MassChromatographicQualityCalculator.calculate(chromatogramSelection, filterSettings.getCodaThreshold(), MOVING_AVERAGE_WINDOW);
			excludedIons = result.getExcludedIons();
		} catch(CodaCalculatorException e) {
			throw new FilterException("A failure occured while calculating the coda mass chromatographic quality.");
		}
		if(excludedIons == null) {
			throw new FilterException("The calculated excluded ions instance is null.");
		}
		/*
		 * Exclude the calculated ions from each scan.
		 */
		IVendorMassSpectrum supplierMassSpectrum;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * Iterate through all selected scans and remove the stored excluded
		 * ions.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			supplierMassSpectrum = chromatogram.getSupplierScan(scan);
			supplierMassSpectrum.removeIons(excludedIons);
		}
	}
	// ----------------------------private methods
}
