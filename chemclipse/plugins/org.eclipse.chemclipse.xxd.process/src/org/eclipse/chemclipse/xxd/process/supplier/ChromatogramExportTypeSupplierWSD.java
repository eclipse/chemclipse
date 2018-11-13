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

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramExportTypeSupplierWSD extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Chromatogram Export [WSD]";

	public ChromatogramExportTypeSupplierWSD() {
		super(CATEGORY, new DataType[]{DataType.WSD});
		IChromatogramConverterSupport support = ChromatogramConverterWSD.getChromatogramConverterSupport();
		for(ISupplier supplier : support.getExportSupplier()) {
			ProcessorSupplier processorSupplier = new ProcessorSupplier(supplier.getId());
			processorSupplier.setName(supplier.getFilterName());
			processorSupplier.setDescription(supplier.getDescription());
			// processorSupplier.setSettingsClass(supplier.getSettingsClass()); // TODO
			addProcessorSupplier(processorSupplier);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = null;
		if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
			String chromatogramExportFolder = PreferenceSupplier.getChromatogramExportFolder();
			File exportFolder = new File(chromatogramExportFolder);
			if(exportFolder.exists()) {
				IChromatogramWSD chromatogramWSD = chromatogramSelectionWSD.getChromatogramWSD();
				File file = new File(chromatogramExportFolder + File.separator + chromatogramWSD.getName());
				processingInfo = ChromatogramConverterWSD.convert(file, chromatogramWSD, processorId, monitor);
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
