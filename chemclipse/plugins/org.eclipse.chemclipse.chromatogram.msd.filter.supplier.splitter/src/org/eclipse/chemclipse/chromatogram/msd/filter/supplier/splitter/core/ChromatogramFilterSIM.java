/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsSIM;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterSIM extends AbstractChromatogramFilterMSD {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> validation = validate(chromatogramSelection, chromatogramFilterSettings);
		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validation);
		//
		FilterSettingsSIM filterSettings;
		if(chromatogramFilterSettings instanceof FilterSettingsSIM filterSettingsSIM) {
			filterSettings = filterSettingsSIM;
		} else {
			filterSettings = PreferenceSupplier.getFilterSettingsSIM();
		}
		int limitIons = filterSettings.getLimitIons();
		//
		if(!processingInfo.hasErrorMessages()) {
			//
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
				/*
				 * Parse the scans.
				 */
				List<IScanMSD> fullScans = new ArrayList<>();
				List<IScanMSD> simScans = new ArrayList<>();
				//
				for(IScan scan : chromatogramMSD.getScans()) {
					if(scan instanceof IScanMSD scanMSD) {
						if(scanMSD.getNumberOfIons() <= limitIons) {
							simScans.add(scanMSD);
						} else {
							fullScans.add(scanMSD);
						}
					}
				}
				/*
				 * Create the SCAN/SIM reference chromatograms.
				 */
				addReferenceChromatogram(chromatogramMSD, fullScans);
				addReferenceChromatogram(chromatogramMSD, simScans);
			}
			//
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram was splitted into SCAN, SIM reference chromatograms."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsSIM settings = PreferenceSupplier.getFilterSettingsSIM();
		return applyFilter(chromatogramSelection, settings, monitor);
	}

	private void addReferenceChromatogram(IChromatogramMSD chromatogramMSD, List<IScanMSD> scansReference) {

		IChromatogramMSD chromatogramReference = new ChromatogramMSD();
		chromatogramReference.setConverterId("");
		for(IScanMSD scan : scansReference) {
			chromatogramReference.addScan(scan);
		}
		chromatogramMSD.addReferencedChromatogram(chromatogramReference);
	}
}
