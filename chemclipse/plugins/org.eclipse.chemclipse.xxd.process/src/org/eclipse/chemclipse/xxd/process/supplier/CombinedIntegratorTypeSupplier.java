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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.CombinedIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.ICombinedIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined.ICombinedIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class CombinedIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Combined Chromatogram and Peak Integrator";

	public CombinedIntegratorTypeSupplier() {
		super(CATEGORY);
		try {
			ICombinedIntegratorSupport support = CombinedIntegrator.getCombinedIntegratorSupport();
			for(String processorId : support.getAvailableIntegratorIds()) {
				ICombinedIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, ALL_DATA_TYPES);
				processorSupplier.setName(supplier.getIntegratorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoIntegratorAvailableException e) {
			// nothing to be done here
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof ICombinedIntegrationSettings) {
			return getProcessingResult(CombinedIntegrator.integrate(chromatogramSelection, (ICombinedIntegrationSettings)processSettings, processorId, monitor), chromatogramSelection);
		} else {
			return getProcessingResult(CombinedIntegrator.integrate(chromatogramSelection, processorId, monitor), chromatogramSelection);
		}
	}
}
