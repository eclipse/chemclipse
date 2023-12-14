/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.IPeakFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.PeakFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.AbstractPeakFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.PeakFilterSettings;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.util.TraceSettingUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakFilter extends AbstractPeakFilter {

	private static final String DESCRIPTION = "Ion Remover Peak Filter";

	@Override
	public IProcessingInfo<IPeakFilterResult> applyFilter(List<IPeakMSD> peaks, IPeakFilterSettings filterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo = new ProcessingInfo<>();
		PeakFilterSettings peakFilterSettings;
		if(filterSettings instanceof PeakFilterSettings settings) {
			peakFilterSettings = settings;
		} else {
			peakFilterSettings = PreferenceSupplier.getPeakFilterSettings();
		}
		TraceSettingUtil settingIon = new TraceSettingUtil();
		IMarkedIons ionsToRemove = new MarkedIons(settingIon.extractTraces(settingIon.deserialize(peakFilterSettings.getIonsToRemove())), MarkedTraceModus.INCLUDE);
		for(IPeakMSD peak : peaks) {
			peak.getTargets().clear();
			IPeakMassSpectrum peakMassSpectrum = peak.getPeakModel().getPeakMassSpectrum();
			peakMassSpectrum.removeIons(ionsToRemove);
		}
		processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The mass spectrum has been optimized successfully."));
		IPeakFilterResult peakFilterResult = new PeakFilterResult(ResultStatus.OK, "The ion remover filter has been applied successfully.");
		processingInfo.setProcessingResult(peakFilterResult);
		return processingInfo;
	}
}
