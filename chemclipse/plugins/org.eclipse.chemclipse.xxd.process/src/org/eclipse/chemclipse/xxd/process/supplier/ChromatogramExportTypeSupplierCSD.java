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
 * Christoph Läubrich - add support for export path configuration, fix name clash for processor ids
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportTypeSupplierCSD extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private static final String PREFIX = "csd.export.";
	public static final String CATEGORY = "Chromatogram Export [CSD]";

	public ChromatogramExportTypeSupplierCSD() {
		super(CATEGORY, new DataType[]{DataType.CSD});
		IChromatogramConverterSupport support = ChromatogramConverterCSD.getInstance().getChromatogramConverterSupport();
		for(ISupplier supplier : support.getExportSupplier()) {
			ProcessorSupplier processorSupplier = new ProcessorSupplier(PREFIX + supplier.getId());
			processorSupplier.setName(supplier.getFilterName());
			processorSupplier.setDescription(supplier.getDescription());
			processorSupplier.setSettingsClass(ChromatogramExportSettings.class);
			addProcessorSupplier(processorSupplier);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processorId.startsWith(PREFIX)) {
			processorId = processorId.substring(PREFIX.length());
		}
		ChromatogramExportSettings settings;
		if(processSettings instanceof ChromatogramExportSettings) {
			settings = (ChromatogramExportSettings)processSettings;
		} else {
			settings = new ChromatogramExportSettings();
		}
		IProcessingInfo processingInfo = null;
		if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
			File exportFolder = new File(settings.getExportFolder());
			if(exportFolder.exists() || exportFolder.mkdirs()) {
				IChromatogramCSD chromatogramCSD = chromatogramSelectionCSD.getChromatogram();
				File file = new File(exportFolder, chromatogramCSD.getName());
				processingInfo = ChromatogramConverterCSD.getInstance().convert(file, chromatogramCSD, processorId, monitor);
			}
		}
		//
		if(processingInfo == null) {
			processingInfo = getProcessingInfoError(processorId);
		}
		//
		return processingInfo;
	}
}
