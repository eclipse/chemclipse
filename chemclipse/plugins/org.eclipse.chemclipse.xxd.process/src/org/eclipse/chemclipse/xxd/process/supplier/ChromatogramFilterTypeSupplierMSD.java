/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add datatypes to supplier
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterTypeSupplierMSD extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.MSD};
	public static final String CATEGORY = "Chromatogram Filter";
	private static final Logger logger = Logger.getLogger(ChromatogramFilterTypeSupplierMSD.class);

	public ChromatogramFilterTypeSupplierMSD() {
		super(CATEGORY);
		try {
			IChromatogramFilterSupportMSD support = ChromatogramFilterMSD.getChromatogramFilterSupport();
			for(String processorId : support.getAvailableFilterIds()) {
				IChromatogramFilterSupplierMSD supplier = support.getFilterSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, DATA_TYPES);
				processorSupplier.setName(supplier.getFilterName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			if(processSettings instanceof IChromatogramFilterSettings) {
				processingInfo = ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, (IChromatogramFilterSettings)processSettings, processorId, monitor);
			} else {
				processingInfo = ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, processorId, monitor);
			}
		} else {
			processingInfo = getProcessingInfoError(processorId);
		}
		return processingInfo;
	}
}
