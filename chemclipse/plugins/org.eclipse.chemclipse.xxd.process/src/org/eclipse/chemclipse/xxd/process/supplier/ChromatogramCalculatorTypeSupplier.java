/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add datatypes
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.ChromatogramCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.exceptions.NoChromatogramCalculatorSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.IChromatogramCalculatorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramCalculatorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD};
	public static final String CATEGORY = "Chromatogram Calculator";
	private static final Logger logger = Logger.getLogger(ChromatogramCalculatorTypeSupplier.class);

	public ChromatogramCalculatorTypeSupplier() {
		super(CATEGORY);
		try {
			IChromatogramCalculatorSupport support = ChromatogramCalculator.getChromatogramCalculatorSupport();
			for(String processorId : support.getAvailableCalculatorIds()) {
				IChromatogramCalculatorSupplier supplier = support.getCalculatorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, DATA_TYPES);
				processorSupplier.setName(supplier.getCalculatorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoChromatogramCalculatorSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof IChromatogramCalculatorSettings) {
			return ChromatogramCalculator.applyCalculator(chromatogramSelection, (IChromatogramCalculatorSettings)processSettings, processorId, monitor);
		} else {
			return ChromatogramCalculator.applyCalculator(chromatogramSelection, processorId, monitor);
		}
	}
}
