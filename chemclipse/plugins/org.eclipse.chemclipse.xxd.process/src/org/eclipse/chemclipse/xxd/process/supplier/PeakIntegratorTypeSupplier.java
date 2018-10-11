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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Peak Integrator";

	public PeakIntegratorTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD, DataType.CSD});
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IPeakIntegratorSupplier integratorSupplier = PeakIntegrator.getPeakIntegratorSupport().getIntegratorSupplier(processorId);
		return integratorSupplier.getIntegratorName();
	}

	@Override
	public String getProcessorDescription(String processorId) throws Exception {

		IPeakIntegratorSupplier integratorSupplier = PeakIntegrator.getPeakIntegratorSupport().getIntegratorSupplier(processorId);
		return integratorSupplier.getDescription();
	}

	@Override
	public List<String> getProcessorIds() throws Exception {

		return PeakIntegrator.getPeakIntegratorSupport().getAvailableIntegratorIds();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		return PeakIntegrator.integrate(chromatogramSelection, processorId, monitor);
	}
}
