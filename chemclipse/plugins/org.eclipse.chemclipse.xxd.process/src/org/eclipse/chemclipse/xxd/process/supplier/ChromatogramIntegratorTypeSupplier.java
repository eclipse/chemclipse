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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.IChromatogramIntegratorSupplier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Integrator";

	@Override
	public String getCategory() {

		return CATEGORY;
	}

	@Override
	public String getSupportedDataTypes() {

		return DataType.MSD.toString() + ", " + DataType.CSD.toString() + ", " + DataType.WSD.toString();
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IChromatogramIntegratorSupplier integratorSupplier = ChromatogramIntegrator.getChromatogramIntegratorSupport().getIntegratorSupplier(processorId);
		return integratorSupplier.getIntegratorName();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return ChromatogramIntegrator.getChromatogramIntegratorSupport().getAvailableIntegratorIds();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProgressMonitor monitor) {

		return ChromatogramIntegrator.integrate(chromatogramSelection, processorId, monitor);
	}
}
