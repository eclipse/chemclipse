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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.core;

import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.detector.BackfoldingShifter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.IBackfoldingSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilterMSD {

	private IBackfoldingSettings backfoldingSettings = null;

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
			applyBackfoldingFilter(chromatogramSelection, monitor);
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection has been successfully backfolded.");
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setChromatogramFilterResult(chromatogramFilterResult);
		return processingInfo;
	}

	// TODO Junit
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
			backfoldingSettings = settings.getBackfoldingSettings();
		}
	}

	private void applyBackfoldingFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) throws FilterException {

		/*
		 * The chromatogram has already been checked by
		 * validate(chromatogramSelection, chromatogramFilterSettings).
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		/*
		 * If backfoldingSettings == null, the default settings will be used.
		 */
		BackfoldingShifter backfoldingShifter = new BackfoldingShifter();
		IExtractedIonSignals extractedIonSignals = backfoldingShifter.shiftIons(chromatogramSelection, backfoldingSettings, monitor);
		IScanMSD massSpectrum;
		IVendorMassSpectrum supplierMassSpectrum;
		/*
		 * Use the start and stop scan range.
		 */
		int startScan = extractedIonSignals.getStartScan();
		int stopScan = extractedIonSignals.getStopScan();
		/*
		 * Iterate through all selected scans and reset the scans.<br/> Each
		 * scan will be replaced by the ions shifted by the
		 * backfolding algorithm.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			massSpectrum = extractedIonSignals.getScan(scan);
			supplierMassSpectrum = chromatogram.getSupplierScan(scan);
			supplierMassSpectrum.removeAllIons();
			for(IIon ion : massSpectrum.getIons()) {
				supplierMassSpectrum.addIon(ion);
			}
		}
	}
	// ----------------------------private methods
}
