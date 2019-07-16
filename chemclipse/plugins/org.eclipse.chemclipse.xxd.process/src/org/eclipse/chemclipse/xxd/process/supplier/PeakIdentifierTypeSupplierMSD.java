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
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentifierTypeSupplierMSD extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Peak Identifier";
	private static final Logger logger = Logger.getLogger(PeakIdentifierTypeSupplierMSD.class);

	public PeakIdentifierTypeSupplierMSD() {
		super(CATEGORY, new DataType[]{DataType.MSD});
		try {
			IPeakIdentifierSupportMSD support = PeakIdentifierMSD.getPeakIdentifierSupport();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierMSD supplier = support.getIdentifierSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId);
				processorSupplier.setName(supplier.getIdentifierName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			if(processSettings instanceof IPeakIdentifierSettingsMSD) {
				processingInfo = PeakIdentifierMSD.identify(chromatogramSelectionMSD, (IPeakIdentifierSettingsMSD)processSettings, processorId, monitor);
			} else {
				processingInfo = PeakIdentifierMSD.identify(chromatogramSelectionMSD, processorId, monitor);
			}
		} else {
			processingInfo = getProcessingInfoError(processorId);
		}
		return processingInfo;
	}
}
