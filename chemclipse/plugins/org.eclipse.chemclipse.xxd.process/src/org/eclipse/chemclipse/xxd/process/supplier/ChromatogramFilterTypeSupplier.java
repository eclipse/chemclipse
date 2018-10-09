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
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Filter";
	private static final Logger logger = Logger.getLogger(ChromatogramCalculatorTypeSupplier.class);
	//
	private IChromatogramFilterSupport support = null;
	private List<String> pluginIds = new ArrayList<>();

	public ChromatogramFilterTypeSupplier() {
		super(new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD});
		//
		try {
			support = ChromatogramFilter.getChromatogramFilterSupport();
			pluginIds = support.getAvailableFilterIds();
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	@Override
	public String getCategory() {

		return CATEGORY;
	}

	@Override
	public Class<? extends IProcessSettings> getProcessSettingsClass(String processorId) throws Exception {

		return support.getFilterSupplier(processorId).getFilterSettingsClass();
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		return support.getFilterSupplier(processorId).getFilterName();
	}

	@Override
	public String getProcessorDescription(String processorId) throws Exception {

		return support.getFilterSupplier(processorId).getDescription();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return pluginIds;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings != null && processSettings instanceof IChromatogramFilterSettings) {
			return ChromatogramFilter.applyFilter(chromatogramSelection, (IChromatogramFilterSettings)processSettings, processorId, monitor);
		} else {
			return ChromatogramFilter.applyFilter(chromatogramSelection, processorId, monitor);
		}
	}
}
