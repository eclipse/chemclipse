/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupport;
import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReportTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Reports";
	private static final Logger logger = Logger.getLogger(ChromatogramReportTypeSupplier.class);

	public ChromatogramReportTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD});
		try {
			IChromatogramReportSupport support = ChromatogramReports.getChromatogramReportSupplierSupport();
			for(String processorId : support.getAvailableProcessorIds()) {
				IChromatogramReportSupplier supplier = support.getReportSupplier(processorId);
				addProcessorId(processorId);
				// addProcessorSettingsClass(processorId, supplier.getSettingsClass()); // TODO
				addProcessorName(processorId, supplier.getReportName());
				addProcessorDescription(processorId, supplier.getDescription());
			}
		} catch(NoReportSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		File file = null; // TODO
		boolean append = false;
		//
		if(processSettings != null && processSettings instanceof IChromatogramReportSettings) {
			return ChromatogramReports.generate(file, append, chromatogram, (IChromatogramReportSettings)processSettings, processorId, monitor);
		} else {
			return ChromatogramReports.generate(file, append, chromatogram, processorId, monitor);
		}
	}
}
