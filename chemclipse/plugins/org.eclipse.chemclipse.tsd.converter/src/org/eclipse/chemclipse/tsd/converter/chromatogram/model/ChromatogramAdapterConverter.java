/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.chromatogram.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.tsd.converter.core.AbstractImportConverter;
import org.eclipse.chemclipse.tsd.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramTSD;
import org.eclipse.chemclipse.tsd.model.core.ScanTSD;
import org.eclipse.chemclipse.tsd.model.core.TypeTSD;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramAdapterConverter extends AbstractImportConverter {

	private TypeTSD typeTSD;

	public ChromatogramAdapterConverter(TypeTSD typeTSD) {

		this.typeTSD = typeTSD;
	}

	@Override
	public IProcessingInfo<IChromatogramTSD> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramTSD> processingInfo = new ProcessingInfo<>();
		//
		IChromatogramTSD chromatogramTSD = null;
		switch(typeTSD) {
			case GC_MS:
				chromatogramTSD = adaptMSD(file, monitor);
				break;
			case HPLC_DAD:
				chromatogramTSD = adaptWSD(file, monitor);
				break;
			default:
				break;
		}
		//
		if(chromatogramTSD != null) {
			processingInfo.setProcessingResult(chromatogramTSD);
		} else {
			processingInfo.addErrorMessage("TSD Adapter", "The adapter type is not supported yet: " + typeTSD.label());
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		return new ProcessingInfo<>();
	}

	@Override
	public IChromatogramTSD convert(InputStream inputStream, IProgressMonitor monitor) throws IOException {

		/*
		 * Support for streams is not needed here yet.
		 */
		return null;
	}

	@Override
	public IChromatogramOverview convertOverview(InputStream inputStream, IProgressMonitor monitor) throws IOException {

		return convert(inputStream, monitor);
	}

	private IChromatogramTSD adaptMSD(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(file, monitor);
		IChromatogramMSD chromatogramMSD = processingInfo.getProcessingResult();
		/*
		 * Determine the trace range
		 */
		int min;
		int max;
		//
		if(PreferenceSupplier.isUseAdapterFixedRangeMSD()) {
			min = PreferenceSupplier.getAdapterMinTraceMSD();
			max = PreferenceSupplier.getAdapterMaxTraceMSD();
		} else {
			min = Integer.MAX_VALUE;
			max = Integer.MIN_VALUE;
			for(IScan scan : chromatogramMSD.getScans()) {
				if(scan instanceof IScanMSD scanMSD) {
					if(scanMSD.getIons().size() >= 2) {
						min = Math.min(min, AbstractIon.getIon(scanMSD.getLowestIon().getIon()));
						max = Math.max(max, AbstractIon.getIon(scanMSD.getHighestIon().getIon()));
					}
				}
			}
		}
		/*
		 * Extract the data
		 */
		List<IScan> scans = new ArrayList<>();
		int size = max - min + 1;
		if(size > 1) {
			for(IScan scan : chromatogramMSD.getScans()) {
				if(scan instanceof IScanMSD scanMSD) {
					IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
					float[] signals = new float[size];
					for(int i = 0; i < size; i++) {
						signals[i] = extractedIonSignal.getAbundance(i + min);
					}
					scans.add(new ScanTSD(scan.getRetentionTime(), signals));
				}
			}
		}
		//
		IChromatogramTSD chromatogram = new ChromatogramAdapterMSD(chromatogramMSD);
		chromatogram.setFile(file);
		chromatogram.addScans(scans);
		//
		return chromatogram;
	}

	private IChromatogramTSD adaptWSD(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramWSD> processingInfo = ChromatogramConverterWSD.getInstance().convert(file, monitor);
		IChromatogramWSD chromatogramWSD = processingInfo.getProcessingResult();
		/*
		 * Determine the trace range
		 */
		int min;
		int max;
		//
		if(PreferenceSupplier.isUseAdapterFixedRangeWSD()) {
			min = PreferenceSupplier.getAdapterMinTraceWSD();
			max = PreferenceSupplier.getAdapterMaxTraceWSD();
		} else {
			min = Integer.MAX_VALUE;
			max = Integer.MIN_VALUE;
			for(IScan scan : chromatogramWSD.getScans()) {
				if(scan instanceof IScanWSD scanWSD) {
					if(scanWSD.getScanSignals().size() >= 2) {
						min = Math.min(min, Math.round(scanWSD.getScanSignals().stream().map(s -> s.getWavelength()).min(Float::compare).orElse(Float.MAX_VALUE)));
						max = Math.max(max, Math.round(scanWSD.getScanSignals().stream().map(s -> s.getWavelength()).max(Float::compare).orElse(Float.MIN_VALUE)));
					}
				}
			}
		}
		/*
		 * Extract the data
		 */
		List<IScan> scans = new ArrayList<>();
		int size = max - min + 1;
		if(size > 1) {
			for(IScan scan : chromatogramWSD.getScans()) {
				if(scan instanceof IScanWSD scanWSD) {
					IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
					float[] signals = new float[size];
					for(int i = 0; i < size; i++) {
						signals[i] = extractedWavelengthSignal.getAbundance(i + min);
					}
					scans.add(new ScanTSD(scan.getRetentionTime(), signals));
				}
			}
		}
		//
		IChromatogramTSD chromatogram = new ChromatogramAdapterWSD(chromatogramWSD);
		chromatogram.setFile(file);
		chromatogram.addScans(scans);
		//
		return chromatogram;
	}
}