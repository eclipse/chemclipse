/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilter extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			//
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				//
				Map<Short, List<IScan>> splittedScanMap = new HashMap<>();
				//
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				for(IScan scan : chromatogramMSD.getScans()) {
					if(scan instanceof IRegularMassSpectrum) {
						IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)scan;
						short massSpectrometer = massSpectrum.getMassSpectrometer();
						List<IScan> scans = splittedScanMap.get(massSpectrometer);
						if(scans == null) {
							scans = new ArrayList<IScan>();
							splittedScanMap.put(massSpectrometer, scans);
						}
						scans.add(scan);
					}
				}
				/*
				 * Create reference chromatograms.
				 */
				List<Short> keys = new ArrayList<>(splittedScanMap.keySet());
				Collections.sort(keys);
				for(short key : keys) {
					/*
					 * Reference MS1, MS2, ...
					 */
					List<IScan> scans = splittedScanMap.get(key);
					IChromatogramMSD chromatogramReference = new ChromatogramMSD();
					chromatogramReference.setConverterId("");
					//
					for(IScan scan : scans) {
						chromatogramReference.addScan(scan);
					}
					//
					chromatogramMSD.addReferencedChromatogram(chromatogramReference);
				}
			}
			//
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram was splitted into MS2, MS3 ... reference chromatograms."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettings splitterSettings = PreferenceSupplier.getSplitterSettings();
		return applyFilter(chromatogramSelection, splitterSettings, monitor);
	}
}
