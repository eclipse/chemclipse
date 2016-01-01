/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.internal.support;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.IPeakFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.PeakFilter;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class PeakFilterTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Filter";
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IPeakFilterSupplier filterSupplier = PeakFilter.getPeakFilterSupport().getFilterSupplier(processorId);
		return filterSupplier.getFilterName();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return PeakFilter.getPeakFilterSupport().getAvailableFilterIds();
	}

	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, String processorId, IProgressMonitor monitor) {

		return PeakFilter.applyFilter(chromatogramSelection, processorId, monitor);
	}
}
