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

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineDetectorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Baseline Detector";
	private static final Logger logger = Logger.getLogger(BaselineDetectorTypeSupplier.class);

	public BaselineDetectorTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD});
		try {
			IBaselineDetectorSupport support = BaselineDetector.getBaselineDetectorSupport();
			for(String processorId : support.getAvailableDetectorIds()) {
				IBaselineDetectorSupplier supplier = support.getBaselineDetectorSupplier(processorId);
				addProcessorId(processorId);
				addProcessorSettingsClass(processorId, supplier.getSettingsClass());
				addProcessorName(processorId, supplier.getDetectorName());
				addProcessorDescription(processorId, supplier.getDescription());
			}
		} catch(NoBaselineDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof IBaselineDetectorSettings) {
			return BaselineDetector.setBaseline(chromatogramSelection, (IBaselineDetectorSettings)processSettings, processorId, monitor);
		} else {
			return BaselineDetector.setBaseline(chromatogramSelection, processorId, monitor);
		}
	}
}
