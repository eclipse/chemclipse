/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterSelection extends AbstractChromatogramFilter implements IChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsSelection filterSettings) {
				/*
				 * Settings
				 */
				double startRetentionTime = filterSettings.getStartRetentionTimeMinutes() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
				double stopRetentionTime = filterSettings.getStopRetentionTimeMinutes() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
				/*
				 * Start Retention Time
				 */
				if(Double.isInfinite(startRetentionTime)) {
					startRetentionTime = chromatogramSelection.getChromatogram().getStartRetentionTime() * Math.signum(startRetentionTime);
				} else if(filterSettings.isStartRelative()) {
					startRetentionTime = chromatogramSelection.getStartRetentionTime() + startRetentionTime;
				}
				/*
				 * Stop Retention Time
				 */
				if(Double.isInfinite(stopRetentionTime)) {
					stopRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime() * Math.signum(startRetentionTime);
				} else if(filterSettings.isStopRelative()) {
					stopRetentionTime = chromatogramSelection.getStopRetentionTime() + stopRetentionTime;
				}
				/*
				 * Validations
				 * Start Retention Time: 0 is allowed. It is implicit a start from the beginning of the chromatogram.
				 */
				if(startRetentionTime >= chromatogramSelection.getChromatogram().getStopRetentionTime()) {
					processingInfo.addMessage(new ProcessingMessage(MessageType.ERROR, Messages.selectRange, Messages.startRetentionTimeOutsideRange));
				}
				//
				if(stopRetentionTime <= chromatogramSelection.getChromatogram().getStartRetentionTime()) {
					processingInfo.addMessage(new ProcessingMessage(MessageType.WARN, Messages.selectRange, Messages.stopRetentionTimeOutsideRange));
				}
				//
				float startAbundance = filterSettings.getStartAbundance();
				if(filterSettings.isStartAbundanceRelative()) {
					startAbundance = chromatogramSelection.getChromatogram().getMaxSignal() * filterSettings.getStartAbundance() / 100;
				}
				float stopAbundance = filterSettings.getStopAbundance();
				if(filterSettings.isStopAbundanceRelative()) {
					stopAbundance = chromatogramSelection.getChromatogram().getMaxSignal() * filterSettings.getStopAbundance() / 100;
				}
				chromatogramSelection.setRanges((int)startRetentionTime, (int)stopRetentionTime, startAbundance, stopAbundance);
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, Messages.chromatogramSelectionApplied));
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsSelection filterSettings = PreferenceSupplier.getFilterSettingsSelection();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}
}
