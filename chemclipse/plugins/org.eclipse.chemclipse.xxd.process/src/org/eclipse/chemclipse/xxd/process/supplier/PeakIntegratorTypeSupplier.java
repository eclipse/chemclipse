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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Peak Integrator";
	private static final Logger logger = Logger.getLogger(PeakIntegratorTypeSupplier.class);

	public PeakIntegratorTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD, DataType.CSD});
		try {
			IPeakIntegratorSupport support = PeakIntegrator.getPeakIntegratorSupport();
			for(String processorId : support.getAvailableIntegratorIds()) {
				IPeakIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				addProcessorId(processorId);
				addProcessorSettingsClass(processorId, supplier.getSettingsClass());
				addProcessorName(processorId, supplier.getIntegratorName());
				addProcessorDescription(processorId, supplier.getDescription());
			}
		} catch(NoIntegratorAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof IPeakIntegrationSettings) {
			return PeakIntegrator.integrate(chromatogramSelection, (IPeakIntegrationSettings)processSettings, processorId, monitor);
		} else {
			return PeakIntegrator.integrate(chromatogramSelection, processorId, monitor);
		}
	}
}
