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

import org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram.ChromatogramIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram.IChromatogramIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram.IChromatogramIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IChromatogramIdentifierSettings;
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

public class ChromatogramIdentifierTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Identifier [MSD]";
	private static final Logger logger = Logger.getLogger(ChromatogramIdentifierTypeSupplier.class);

	public ChromatogramIdentifierTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD});
		try {
			IChromatogramIdentifierSupport support = ChromatogramIdentifier.getChromatogramIdentifierSupport();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IChromatogramIdentifierSupplier supplier = support.getIdentifierSupplier(processorId);
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

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			if(processSettings instanceof IChromatogramIdentifierSettings) {
				return ChromatogramIdentifier.identify(chromatogramSelectionMSD, (IChromatogramIdentifierSettings)processSettings, processorId, monitor);
			} else {
				return ChromatogramIdentifier.identify(chromatogramSelectionMSD, processorId, monitor);
			}
		} else {
			return getProcessingInfoError(processorId);
		}
	}
}
