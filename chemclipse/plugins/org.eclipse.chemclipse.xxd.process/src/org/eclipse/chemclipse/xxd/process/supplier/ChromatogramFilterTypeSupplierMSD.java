/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupplierMSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterTypeSupplierMSD extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Filter [MSD]";

	public ChromatogramFilterTypeSupplierMSD() {
		super(new DataType[]{DataType.MSD});
	}

	@Override
	public String getCategory() {

		return CATEGORY;
	}

	@Override
	public Class<? extends IProcessSettings> getProcessSettingsClass(String processorId) throws Exception {

		IChromatogramFilterSupplierMSD filterSupplier = ChromatogramFilterMSD.getChromatogramFilterSupport().getFilterSupplier(processorId);
		return filterSupplier.getFilterSettingsClass();
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IChromatogramFilterSupplierMSD filterSupplier = ChromatogramFilterMSD.getChromatogramFilterSupport().getFilterSupplier(processorId);
		return filterSupplier.getFilterName();
	}

	@Override
	public String getProcessorDescription(String processorId) throws Exception {

		IChromatogramFilterSupplierMSD filterSupplier = ChromatogramFilterMSD.getChromatogramFilterSupport().getFilterSupplier(processorId);
		return filterSupplier.getDescription();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return ChromatogramFilterMSD.getChromatogramFilterSupport().getAvailableFilterIds();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			processingInfo = ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, processorId, monitor);
		} else {
			processingInfo = new ProcessingInfo();
			processingInfo.addErrorMessage(processorId, "The data is not supported by the processor.");
		}
		return processingInfo;
	}
}
