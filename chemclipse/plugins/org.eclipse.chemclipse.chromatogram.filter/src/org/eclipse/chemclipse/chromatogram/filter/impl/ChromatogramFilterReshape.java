/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsReshape;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ChromatogramWSD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.implementation.ChromatogramISD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterReshape extends AbstractChromatogramFilter implements IChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsReshape filterSettings) {
				/*
				 * Settings
				 */
				HeaderField headerField = filterSettings.getHeaderField();
				double segmentWidthMinutes = filterSettings.getSegmentWidthMinutes();
				boolean resetRetentionTimes = filterSettings.isResetRetentionTimes();
				int segmentWidth = (int)Math.round(segmentWidthMinutes * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram.getStopRetentionTime() > segmentWidth) {
					/*
					 * Collect
					 */
					int scanInterval = chromatogram.getScanInterval();
					List<List<IScan>> stackedScans = new ArrayList<>();
					List<IScan> scans = null;
					int offset = 0;
					for(IScan scan : chromatogram.getScans()) {
						int retentionTime = scan.getRetentionTime();
						if(scans == null) {
							scans = new ArrayList<>();
							scans.add(scan);
							stackedScans.add(scans);
							offset = retentionTime;
						} else if((retentionTime - offset) > segmentWidth) {
							scans = new ArrayList<>();
							scans.add(scan);
							stackedScans.add(scans);
							offset = retentionTime;
						} else {
							scans.add(scan);
						}
					}
					/*
					 * Assign
					 */
					if(!stackedScans.isEmpty()) {
						/*
						 * Master
						 */
						chromatogram.removeAllPeaks();
						chromatogram.replaceAllScans(stackedScans.get(0));
						assignIdentifier(chromatogram, headerField, "Cut 1");
						calculateScanIntervalAndDelay(chromatogram, resetRetentionTimes, 0, scanInterval);
						/*
						 * References
						 */
						for(int i = 1; i < stackedScans.size(); i++) {
							IChromatogram<?> chromatogramReference = createChromatogramReference(chromatogram);
							assignIdentifier(chromatogramReference, headerField, "Cut " + (i + 1));
							chromatogramReference.addScans(stackedScans.get(i));
							calculateScanIntervalAndDelay(chromatogramReference, resetRetentionTimes, 0, scanInterval);
							chromatogram.addReferencedChromatogram(chromatogramReference);
						}
					}
				}
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsReshape filterSettings = PreferenceSupplier.getFilterSettingsReshape();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void assignIdentifier(IChromatogram<?> chromatogram, HeaderField headerField, String identifier) {

		switch(headerField) {
			case NAME:
				chromatogram.setFile(new File(identifier));
				break;
			case DATA_NAME:
				chromatogram.setDataName(identifier);
				break;
			case MISC_INFO:
				chromatogram.setMiscInfo(identifier);
				break;
			case SAMPLE_GROUP:
				chromatogram.setSampleGroup(identifier);
				break;
			case SAMPLE_NAME:
				chromatogram.setSampleName(identifier);
				break;
			case SHORT_INFO:
				chromatogram.setShortInfo(identifier);
				break;
			default:
				break;
		}
	}

	private void calculateScanIntervalAndDelay(IChromatogram<?> chromatogram, boolean resetRetentionTimes, int scanDelay, int scanInterval) {

		/*
		 * First recalculate on demand.
		 */
		if(resetRetentionTimes) {
			chromatogram.setScanDelay(0);
			chromatogram.setScanInterval(scanInterval);
			chromatogram.recalculateRetentionTimes();
		}
		/*
		 * Recalculate completely.
		 */
		int startRetentionTime = chromatogram.getStartRetentionTime();
		int stopRetentionTime = chromatogram.getStopRetentionTime();
		float deltaRetentionTime = stopRetentionTime - startRetentionTime + 1;
		int numberOfScans = chromatogram.getNumberOfScans();
		/*
		 * Delay
		 */
		if(startRetentionTime > 0) {
			scanDelay = startRetentionTime;
		}
		/*
		 * Interval
		 */
		if(numberOfScans > 0 && deltaRetentionTime > 0) {
			float calculation = deltaRetentionTime / numberOfScans / 10.0f;
			scanInterval = Math.round(calculation) * 10;
		}
		/*
		 * Adjust the retention times.
		 */
		chromatogram.setScanDelay(scanDelay);
		chromatogram.setScanInterval(scanInterval);
		chromatogram.recalculateRetentionTimes();
	}

	private IChromatogram<?> createChromatogramReference(IChromatogram<?> chromatogram) {

		IChromatogram<?> chromatogramReference = null;
		//
		if(chromatogram instanceof IChromatogramCSD) {
			chromatogramReference = new ChromatogramCSD();
		} else if(chromatogram instanceof IChromatogramMSD) {
			chromatogramReference = new ChromatogramMSD();
		} else if(chromatogram instanceof IChromatogramWSD) {
			chromatogramReference = new ChromatogramWSD();
		} else if(chromatogram instanceof IChromatogramISD) {
			chromatogramReference = new ChromatogramISD();
		}
		//
		if(chromatogramReference != null) {
			chromatogramReference.setFile(chromatogram.getFile());
			chromatogramReference.setConverterId(chromatogram.getConverterId());
		}
		//
		return chromatogramReference;
	}
}