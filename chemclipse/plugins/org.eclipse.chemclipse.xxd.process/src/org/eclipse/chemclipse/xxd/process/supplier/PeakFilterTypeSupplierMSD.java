/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoPeakFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.IPeakFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.IPeakFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.PeakFilter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakFilterTypeSupplierMSD extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Peak Filter [MSD]";
	private static final Logger logger = Logger.getLogger(PeakFilterTypeSupplierMSD.class);

	public PeakFilterTypeSupplierMSD() {
		super(CATEGORY, new DataType[]{DataType.MSD});
		try {
			IPeakFilterSupport support = PeakFilter.getPeakFilterSupport();
			for(String processorId : support.getAvailableFilterIds()) {
				IPeakFilterSupplier supplier = support.getFilterSupplier(processorId);
				addProcessorId(processorId);
				// addProcessorSettingsClass(processorId, supplier.getSettingsClass()); // TODO
				addProcessorName(processorId, supplier.getFilterName());
				addProcessorDescription(processorId, supplier.getDescription());
			}
		} catch(NoPeakFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			if(processSettings instanceof IPeakFilterSettings) {
				processingInfo = PeakFilter.applyFilter(chromatogramSelectionMSD, (IPeakFilterSettings)processSettings, processorId, monitor);
			} else {
				processingInfo = PeakFilter.applyFilter(chromatogramSelectionMSD, processorId, monitor);
			}
		} else {
			processingInfo = new ProcessingInfo();
			processingInfo.addErrorMessage(processorId, "The data is not supported by the processor.");
		}
		return processingInfo;
	}
}
