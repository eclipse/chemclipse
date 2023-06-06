/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsReset;
import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterReset extends AbstractChromatogramFilter implements IChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsReset) {
				chromatogramSelection.reset();
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, Messages.chromatogramSelectionReset));
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsReset filterSettings = PreferenceSupplier.getFilterSettingsReset();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}
}
