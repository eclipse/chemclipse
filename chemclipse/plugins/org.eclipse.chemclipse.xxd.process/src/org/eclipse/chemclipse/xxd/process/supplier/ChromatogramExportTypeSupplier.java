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
 * Christoph Läubrich - add support for export path configuration, fix name clash for processor ids, generics, error reporting, unify msd/csd/wsd
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	private static final String PREFIX_CSD = "csd.export.";
	private static final String PREFIX_MSD = "msd.export.";
	public static final String CATEGORY = "Chromatogram Export";
	private static final String PREFIX_WSD = "wsd.export.";
	// we use this to hold the file extension of the supplier and also for backcompat processor ids
	private Map<String, String> extCSD = new HashMap<>();
	private Map<String, String> extMSD = new HashMap<>();
	private Map<String, String> extWSD = new HashMap<>();

	public ChromatogramExportTypeSupplier() {
		super(CATEGORY);
		for(ISupplier supplier : ChromatogramConverterCSD.getInstance().getChromatogramConverterSupport().getExportSupplier()) {
			String id = supplier.getId();
			ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX_CSD + id, CSD_DATA_TYPES);
			processorSupplier.setName(supplier.getFilterName());
			processorSupplier.setDescription(supplier.getDescription());
			processorSupplier.setSettingsClass(ChromatogramExportSettings.class);
			addProcessorSupplier(processorSupplier);
			extCSD.put(id, supplier.getFileExtension());
		}
		for(ISupplier supplier : ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport().getExportSupplier()) {
			String id = supplier.getId();
			ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX_MSD + id, MSD_DATA_TYPES);
			processorSupplier.setName(supplier.getFilterName());
			processorSupplier.setDescription(supplier.getDescription());
			processorSupplier.setSettingsClass(ChromatogramExportSettings.class);
			addProcessorSupplier(processorSupplier);
			extMSD.put(id, supplier.getFileExtension());
		}
		for(ISupplier supplier : ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport().getExportSupplier()) {
			String id = supplier.getId();
			ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX_WSD + id, WSD_DATA_TYPES);
			processorSupplier.setName(supplier.getFilterName());
			processorSupplier.setDescription(supplier.getDescription());
			processorSupplier.setSettingsClass(ChromatogramExportSettings.class);
			addProcessorSupplier(processorSupplier);
			extWSD.put(id, supplier.getFileExtension());
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		ChromatogramExportSettings settings;
		if(processSettings instanceof ChromatogramExportSettings) {
			settings = (ChromatogramExportSettings)processSettings;
		} else {
			settings = new ChromatogramExportSettings();
		}
		File exportFolder = settings.getExportFolder();
		if(exportFolder == null) {
			return getProcessingInfoError(processorId, "No outputfolder specified and no default configured");
		}
		if(exportFolder.exists() || exportFolder.mkdirs()) {
			boolean startsWithCSD = processorId.startsWith(PREFIX_CSD);
			boolean startsWithMSD = processorId.startsWith(PREFIX_MSD);
			boolean startsWithWSD = processorId.startsWith(PREFIX_WSD);
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			IProcessingInfo<File> info;
			if(startsWithCSD || extCSD.containsKey(processorId)) {
				if(startsWithCSD) {
					processorId = processorId.substring(PREFIX_CSD.length());
				}
				if(chromatogram instanceof IChromatogramCSD) {
					File file = getExportFileName(settings, extCSD.getOrDefault(processorId, ".bin"), chromatogram);
					info = ChromatogramConverterCSD.getInstance().convert(file, (IChromatogramCSD)chromatogram, processorId, monitor);
					info.setProcessingResult(file);
				} else {
					return getProcessingInfoError(processorId);
				}
			} else if(startsWithMSD || extMSD.containsKey(processorId)) {
				if(startsWithMSD) {
					processorId = processorId.substring(PREFIX_MSD.length());
				}
				if(chromatogram instanceof IChromatogramMSD) {
					File file = getExportFileName(settings, extMSD.getOrDefault(processorId, ".bin"), chromatogram);
					info = ChromatogramConverterMSD.getInstance().convert(file, (IChromatogramMSD)chromatogram, processorId, monitor);
					info.setProcessingResult(file);
				} else {
					return getProcessingInfoError(processorId);
				}
			} else if(startsWithWSD || extWSD.containsKey(processorId)) {
				if(startsWithWSD) {
					processorId = processorId.substring(PREFIX_WSD.length());
				}
				if(chromatogram instanceof IChromatogramWSD) {
					File file = getExportFileName(settings, extWSD.getOrDefault(processorId, ".bin"), chromatogram);
					info = ChromatogramConverterWSD.getInstance().convert(file, (IChromatogramWSD)chromatogram, processorId, monitor);
					info.setProcessingResult(file);
				} else {
					return getProcessingInfoError(processorId);
				}
			} else {
				return null;
			}
			File result = info.getProcessingResult();
			if(result != null) {
				info.addInfoMessage(processorId, "Exported data to " + result.getAbsolutePath());
			}
			return getProcessingResult(info, chromatogramSelection);
		} else {
			return getProcessingInfoError(processorId, "The specified outputfolder does not exits and can't be created");
		}
	}

	public File getExportFileName(ChromatogramExportSettings settings, String extension, IChromatogram<?> chromatogram) {

		return new File(settings.getExportFolder(), settings.getFileNamePattern().replace(ChromatogramExportSettings.VARIABLE_CHROMATOGRAM_NAME, chromatogram.getName()).replace(ChromatogramExportSettings.VARIABLE_EXTENSION, extension));
	}
}
