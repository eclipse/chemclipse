/*******************************************************************************
 * Copyright (c) 2018,2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - settings support, error handling
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
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReportTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD};
	public static final String CATEGORY = "Chromatogram Reports";
	private static final Logger logger = Logger.getLogger(ChromatogramReportTypeSupplier.class);

	public ChromatogramReportTypeSupplier() {
		super(CATEGORY);
		try {
			IChromatogramReportSupport support = ChromatogramReports.getChromatogramReportSupplierSupport();
			for(String processorId : support.getAvailableProcessorIds()) {
				IChromatogramReportSupplier supplier = support.getReportSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, DATA_TYPES);
				processorSupplier.setName(supplier.getReportName());
				processorSupplier.setDescription(supplier.getDescription());
				Class<? extends IChromatogramReportSettings> settingsClass = supplier.getSettingsClass();
				if(settingsClass == null) {
					settingsClass = DefaultChromatogramReportSettings.class;
				}
				processorSupplier.setSettingsClass(settingsClass);
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoReportSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IChromatogramReportSettings settings;
		if(processSettings instanceof IChromatogramReportSettings) {
			settings = (IChromatogramReportSettings)processSettings;
		} else {
			settings = new DefaultChromatogramReportSettings();
		}
		if(chromatogramSelection != null) {
			File exportFolder = settings.getExportFolder();
			if(exportFolder == null) {
				return getProcessingInfoError(processorId, "No outputfolder specified and no default configured");
			}
			String extension;
			try {
				extension = ChromatogramReports.getChromatogramReportSupplierSupport().getReportSupplier(processorId).getFileExtension();
			} catch(NoReportSupplierAvailableException e) {
				return getProcessingInfoError(processorId, "Processor not found");
			}
			if(exportFolder.exists() || exportFolder.mkdirs()) {
				IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
				File file = new File(exportFolder, settings.getFileNamePattern().replace(IChromatogramReportSettings.VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName()).replace(IChromatogramReportSettings.VARIABLE_EXTENSION, extension));
				IProcessingInfo info = ChromatogramReports.generate(file, settings.isAppend(), chromatogram, settings, processorId, monitor);
				info.addInfoMessage(processorId, "Report written to " + file.getAbsolutePath());
				return info;
			} else {
				return getProcessingInfoError(processorId, "The specified outputfolder does not exits and can't be created");
			}
		} else {
			return getProcessingInfoError(processorId);
		}
	}
}
