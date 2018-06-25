/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core.internal.support.RetentionTimeStretcher;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ISupplierFilterStretchSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterStretch extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, chromatogramFilterSettings));
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}
		/*
		 * Try to shift the retention times.
		 */
		IChromatogramFilterResult chromatogramFilterResult;
		try {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram has been stretched successfully.");
			RetentionTimeStretcher.stretchChromatogram(chromatogramSelection, getSupplierFilterSettings(chromatogramFilterSettings));
		} catch(FilterException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		processingInfo.setProcessingResult(chromatogramFilterResult);
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getChromatogramFilterSettingsStretch();
		return applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
	}

	private ISupplierFilterStretchSettings getSupplierFilterSettings(IChromatogramFilterSettings chromatogramFilterSettings) {

		/*
		 * Get the excluded ions instance.
		 */
		if(chromatogramFilterSettings instanceof ISupplierFilterStretchSettings) {
			return (ISupplierFilterStretchSettings)chromatogramFilterSettings;
		} else {
			return PreferenceSupplier.getChromatogramFilterSettingsStretch();
		}
	}
}
