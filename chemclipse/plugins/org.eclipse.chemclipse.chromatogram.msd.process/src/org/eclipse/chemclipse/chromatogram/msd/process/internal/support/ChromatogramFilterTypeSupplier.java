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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class ChromatogramFilterTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Filter";
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IChromatogramFilterSupplier filterSupplier = ChromatogramFilter.getChromatogramFilterSupport().getFilterSupplier(processorId);
		return filterSupplier.getFilterName();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return ChromatogramFilter.getChromatogramFilterSupport().getAvailableFilterIds();
	}

	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelectionMSD chromatogramSelection, String processorId, IProgressMonitor monitor) {

		return ChromatogramFilter.applyFilter(chromatogramSelection, processorId, monitor);
	}
}
