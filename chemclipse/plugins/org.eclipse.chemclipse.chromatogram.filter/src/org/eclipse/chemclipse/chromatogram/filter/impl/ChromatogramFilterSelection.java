/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - more feedback for invalid inputs
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsSelection;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterSelection extends AbstractChromatogramFilter implements IChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsSelection) {
				FilterSettingsSelection filterSettings = (FilterSettingsSelection)chromatogramFilterSettings;
				double startRT = filterSettings.getStartRetentionTimeMinutes() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
				double stopRT = filterSettings.getStopRetentionTimeMinutes() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
				if(Double.isInfinite(startRT)) {
					startRT = chromatogramSelection.getChromatogram().getStartRetentionTime() * Math.signum(startRT);
				} else if(filterSettings.isStartRelative()) {
					startRT = chromatogramSelection.getStartRetentionTime() + startRT;
				}
				if(Double.isInfinite(stopRT)) {
					stopRT = chromatogramSelection.getChromatogram().getStopRetentionTime() * Math.signum(startRT);
				} else if(filterSettings.isStopRelative()) {
					stopRT = chromatogramSelection.getStopRetentionTime() + stopRT;
				}
				if(startRT < chromatogramSelection.getChromatogram().getStartRetentionTime()) {
					processingInfo.addMessage(new ProcessingMessage(MessageType.ERROR, "Select Range", "Start RT is outside chromatogram range."));
				}
				if(stopRT > chromatogramSelection.getChromatogram().getStopRetentionTime()) {
					processingInfo.addMessage(new ProcessingMessage(MessageType.WARN, "Select Range", "Stop RT is outside chromatogram range."));
				}
				chromatogramSelection.setRangeRetentionTime((int)startRT, (int)stopRT);
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Chromatogram Selection applied"));
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsSelection filterSettings = PreferenceSupplier.getFilterSettingsSelection();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}
}
