/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for export path configuration, fix name clash for processor ids, generics, error reporting
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportTypeSupplierMSD extends AbstractProcessTypeSupplier<File> {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.MSD};
	public static final String CATEGORY = "Chromatogram Export";
	private static final String PREFIX = "msd.export.";

	public ChromatogramExportTypeSupplierMSD() {
		super(CATEGORY);
		IChromatogramConverterSupport support = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
		for(ISupplier supplier : support.getExportSupplier()) {
			ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX + supplier.getId(), DATA_TYPES);
			processorSupplier.setName(supplier.getFilterName());
			processorSupplier.setDescription(supplier.getDescription());
			processorSupplier.setSettingsClass(ChromatogramExportSettings.class);
			addProcessorSupplier(processorSupplier);
		}
	}

	@Override
	public IProcessingInfo<File> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processorId.startsWith(PREFIX)) {
			processorId = processorId.substring(PREFIX.length());
		}
		ChromatogramExportSettings settings;
		if(processSettings instanceof ChromatogramExportSettings) {
			settings = (ChromatogramExportSettings)processSettings;
		} else {
			settings = new ChromatogramExportSettings();
		}
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			File exportFolder = settings.getExportFolder();
			if(exportFolder == null) {
				return getProcessingInfoError(processorId, "No outputfolder specified and no default configured");
			}
			String extension;
			try {
				extension = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport().getSupplier(processorId).getFileExtension();
			} catch(NoConverterAvailableException e) {
				return getProcessingInfoError(processorId, "Processor not found");
			}
			if(exportFolder.exists() || exportFolder.mkdirs()) {
				IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogram();
				File file = new File(exportFolder, settings.getFileNamePattern().replace(IChromatogramReportSettings.VARIABLE_CHROMATOGRAM_NAME, chromatogramMSD.getName()).replace(ChromatogramExportSettings.VARIABLE_EXTENSION, extension));
				IProcessingInfo<File> info = ChromatogramConverterMSD.getInstance().convert(file, chromatogramMSD, processorId, monitor);
				info.addInfoMessage(processorId, "Exported data to " + file.getAbsolutePath());
				return info;
			} else {
				return getProcessingInfoError(processorId, "The specified outputfolder does not exits and can't be created");
			}
		} else {
			return getProcessingInfoError(processorId);
		}
	}
}
