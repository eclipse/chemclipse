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

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupplier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineDetectorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Baseline Detector";

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

		IBaselineDetectorSupplier baselineSupplier = BaselineDetector.getBaselineDetectorSupport().getBaselineDetectorSupplier(processorId);
		return baselineSupplier.getDetectorName();
	}

	@Override
	public String getProcessorDescription(String processorId) throws Exception {

		IBaselineDetectorSupplier baselineSupplier = BaselineDetector.getBaselineDetectorSupport().getBaselineDetectorSupplier(processorId);
		return baselineSupplier.getDescription();
	}

	@Override
	public List<String> getPluginIds() throws Exception {

		return BaselineDetector.getBaselineDetectorSupport().getAvailableDetectorIds();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProgressMonitor monitor) {

		return BaselineDetector.setBaseline(chromatogramSelection, processorId, monitor);
	}
}
