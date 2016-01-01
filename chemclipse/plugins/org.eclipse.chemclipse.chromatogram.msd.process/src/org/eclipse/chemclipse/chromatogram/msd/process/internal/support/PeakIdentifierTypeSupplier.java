/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.identifier.core.ISupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class PeakIdentifierTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Identifier";
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		ISupplier peakIdentifierSupplier = PeakIdentifier.getPeakIdentifierSupport().getIdentifierSupplier(processorId);
		return peakIdentifierSupplier.getIdentifierName();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return PeakIdentifier.getPeakIdentifierSupport().getAvailableIdentifierIds();
	}

	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, String processorId, IProgressMonitor monitor) {

		return PeakIdentifier.identify(getPeakList(chromatogramSelection), processorId, monitor);
	}

	private List<IPeakMSD> getPeakList(IChromatogramSelectionMSD chromatogramSelection) {

		/*
		 * TODO make generic
		 * May use a better generic supertype, e.g <? extends IPeak>???
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks(chromatogramSelection);
		List<IPeakMSD> peakList = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD chromatogramPeak : peaks) {
			peakList.add(chromatogramPeak);
		}
		return peakList;
	}
}
