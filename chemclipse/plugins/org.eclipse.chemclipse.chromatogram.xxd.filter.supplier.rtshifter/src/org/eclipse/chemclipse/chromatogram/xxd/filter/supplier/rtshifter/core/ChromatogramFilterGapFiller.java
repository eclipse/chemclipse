/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsGapFiller;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterGapFiller extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsGapFiller) {
				FilterSettingsGapFiller filterSettings = (FilterSettingsGapFiller)chromatogramFilterSettings;
				autofillScans(chromatogramSelection, filterSettings);
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram has been autofilled successfully."));
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsGapFiller filterSettings = PreferenceSupplier.getFilterSettingsFillGaps();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	@SuppressWarnings("unchecked")
	private void autofillScans(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsGapFiller filterSettings) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		//
		int scanInterval = chromatogram.getScanInterval();
		if(scanInterval > 0) {
			/*
			 * Settings
			 */
			List<IScan> scansFillGaps = new ArrayList<>();
			/*
			 * At least 3 * scan interval, because two scans are added in case of a gap, each with a delta of scan interval.
			 */
			int limitFactor = filterSettings.getLimitFactor() >= 3 ? filterSettings.getLimitFactor() : 3;
			int limit = scanInterval * limitFactor;
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime()) + 1;
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			//
			for(int i = startScan; i <= stopScan; i++) {
				/*
				 * Try to detect the gap.
				 */
				IScan scan0 = chromatogram.getScan(i - 1);
				IScan scan1 = chromatogram.getScan(i);
				//
				int retentionTime0 = scan0.getRetentionTime();
				int retentionTime1 = scan1.getRetentionTime();
				int interval = retentionTime1 - retentionTime0;
				//
				if(interval > limit) {
					/*
					 * Full
					 */
					retentionTime0 += scanInterval;
					retentionTime1 -= scanInterval;
					float intensity0 = scan0.getTotalSignal() >= 0 ? Float.MIN_VALUE : -Float.MIN_VALUE;
					float intensity1 = scan1.getTotalSignal() >= 0 ? Float.MIN_VALUE : -Float.MIN_VALUE;
					//
					if(scan0 instanceof IScanCSD) {
						scansFillGaps.add(new ScanCSD(retentionTime0, intensity0));
						int deltaStep = (retentionTime1 - retentionTime0) / scanInterval;
						if(deltaStep > 0) {
							int retentionTime = retentionTime0;
							int next = retentionTime1 - deltaStep;
							while(retentionTime < next) {
								retentionTime += deltaStep;
								scansFillGaps.add(new ScanCSD(retentionTime, intensity0));
							}
						}
						scansFillGaps.add(new ScanCSD(retentionTime1, intensity1));
					} else if(scan0 instanceof IScanMSD) {
						try {
							IScanMSD scanAdd0 = new ScanMSD();
							scanAdd0.setRetentionTime(retentionTime0);
							scanAdd0.addIon(new Ion(1, intensity0));
							scansFillGaps.add(scanAdd0);
							//
							IScanMSD scanAdd1 = new ScanMSD();
							scanAdd1.setRetentionTime(retentionTime1);
							scanAdd1.addIon(new Ion(1, intensity1));
							scansFillGaps.add(scanAdd1);
						} catch(Exception e) {
							//
						}
					} else if(scan0 instanceof IScanWSD) {
						try {
							IScanWSD scanAdd0 = new ScanWSD();
							scanAdd0.setRetentionTime(retentionTime0);
							scanAdd0.addScanSignal(new ScanSignalWSD(1, intensity0));
							scansFillGaps.add(scanAdd0);
							//
							IScanWSD scanAdd1 = new ScanWSD();
							scanAdd1.setRetentionTime(retentionTime1);
							scanAdd1.addScanSignal(new ScanSignalWSD(1, intensity1));
							scansFillGaps.add(scanAdd1);
						} catch(Exception e) {
							//
						}
					}
				}
			}
			/*
			 * Insert new scans and sort them by retention time.
			 */
			if(scansFillGaps.size() > 0) {
				List<IScan> scans = new ArrayList<>(chromatogram.getScans());
				scans.addAll(scansFillGaps);
				Collections.sort(scans, (s1, s2) -> Integer.compare(s1.getRetentionTime(), s2.getRetentionTime()));
				int from = 1;
				int to = chromatogram.getNumberOfScans();
				chromatogram.removeScans(from, to);
				chromatogram.addScans(scans);
			}
		}
	}
}
