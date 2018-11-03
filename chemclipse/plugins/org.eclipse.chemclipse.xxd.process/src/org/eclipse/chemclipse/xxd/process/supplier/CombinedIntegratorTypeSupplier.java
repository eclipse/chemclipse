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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.CombinedIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.ICombinedIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.ICombinedIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class CombinedIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Combined Chromatogram and Peak Integrator";
	private static final Logger logger = Logger.getLogger(CombinedIntegratorTypeSupplier.class);

	public CombinedIntegratorTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD});
		try {
			ICombinedIntegratorSupport support = CombinedIntegrator.getCombinedIntegratorSupport();
			for(String processorId : support.getAvailableIntegratorIds()) {
				ICombinedIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				addProcessorId(processorId);
				// addProcessorSettingsClass(processorId, supplier.getSettingsClass()); // TODO
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

		if(processSettings instanceof ICombinedIntegrationSettings) {
			return CombinedIntegrator.integrate(chromatogramSelection, (ICombinedIntegrationSettings)processSettings, processorId, monitor);
		} else {
			return CombinedIntegrator.integrate(chromatogramSelection, processorId, monitor);
		}
	}
}
