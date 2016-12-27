/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator.IMassChromatographicQualityResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator.MassChromatographicQualityCalculator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.CodaCalculatorException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.CodaSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.ICodaSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	// TODO als Option in CodaSettings?
	private static WindowSize MOVING_AVERAGE_WINDOW = WindowSize.SCANS_5;
	private ICodaSettings codaSettings = null;

	// TODO IProgressMonitor
	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramFilterProcessingInfo processingInfo = new ChromatogramFilterProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Try to fold back the chromatogram selection.
		 */
		setFilterSettings(chromatogramFilterSettings);
		IChromatogramFilterResult chromatogramFilterResult;
		try {
			applyCodaFilter(chromatogramSelection);
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection has been successfully cleaned by the coda algorithm.");
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setChromatogramFilterResult(chromatogramFilterResult);
		return processingInfo;
	}

	// TODO JUnit
	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	// ----------------------------private methods
	private void setFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		/*
		 * Get the excluded ions instance.
		 */
		if(chromatogramFilterSettings instanceof ISupplierFilterSettings) {
			ISupplierFilterSettings settings = (ISupplierFilterSettings)chromatogramFilterSettings;
			codaSettings = settings.getCodaSettings();
		} else {
			codaSettings = new CodaSettings();
		}
	}

	private void applyCodaFilter(IChromatogramSelectionMSD chromatogramSelection) throws FilterException {

		/*
		 * Try to get the ions, that should be excluded.
		 */
		IMarkedIons excludedIons;
		try {
			IMassChromatographicQualityResult result = MassChromatographicQualityCalculator.calculate(chromatogramSelection, codaSettings.getCodaThreshold(), MOVING_AVERAGE_WINDOW);
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
