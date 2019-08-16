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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.IChromatogramIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.IChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Integrator";

	public ChromatogramIntegratorTypeSupplier() {
		super(CATEGORY);
		try {
			IChromatogramIntegratorSupport support = ChromatogramIntegrator.getChromatogramIntegratorSupport();
			for(String processorId : support.getAvailableIntegratorIds()) {
				IChromatogramIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, ALL_DATA_TYPES);
				processorSupplier.setName(supplier.getIntegratorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoIntegratorAvailableException e) {
			// nothing to do...
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof IChromatogramIntegrationSettings) {
			return getProcessingResult(ChromatogramIntegrator.integrate(chromatogramSelection, (IChromatogramIntegrationSettings)processSettings, processorId, monitor), chromatogramSelection);
		} else {
			return getProcessingResult(ChromatogramIntegrator.integrate(chromatogramSelection, processorId, monitor), chromatogramSelection);
		}
	}
}
