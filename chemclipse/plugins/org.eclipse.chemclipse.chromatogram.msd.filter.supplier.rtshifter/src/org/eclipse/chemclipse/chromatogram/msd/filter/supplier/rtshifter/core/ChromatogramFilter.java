/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.core;

import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.core.internal.support.RTShifter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramFilter {

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramFilterProcessingInfo processingInfo = new ChromatogramFilterProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Try to shift the retention times.
		 */
		IChromatogramFilterResult chromatogramFilterResult;
		try {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram has been shifted successfully.");
			RTShifter.shiftRetentionTimes(chromatogramSelection, getSupplierFilterSettings(chromatogramFilterSettings));
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setChromatogramFilterResult(chromatogramFilterResult);
		return processingInfo;
	}

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettings();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	private ISupplierFilterSettings getSupplierFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		/*
		 * Get the excluded ions instance.
		 */
		if(chromatogramFilterSettings instanceof ISupplierFilterSettings) {
			return (ISupplierFilterSettings)chromatogramFilterSettings;
		} else {
			return PreferenceSupplier.getChromatogramFilterSettings();
		}
	}
}
