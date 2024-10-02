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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsReshape;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.ChromatogramSupport;
import org.eclipse.chemclipse.model.targets.TargetSupport;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.ISignalInfrared;
import org.eclipse.chemclipse.vsd.model.core.ISignalRaman;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.implementation.ChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.implementation.ScanVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.vsd.model.implementation.SignalRaman;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanWSD;
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
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				HeaderField headerField = filterSettings.getHeaderField();
				double segmentWidthDefault = filterSettings.getSegmentWidthDefault();
				boolean resetRetentionTimes = filterSettings.isResetRetentionTimes();
				String recurringPeakName = filterSettings.getRecurringPeakName();
				//
				int segmentWidth = 0;
				switch(filterSettings.getRangeOption()) {
					case RETENTION_TIME_MIN:
						/*
						 * Minutes
						 */
						segmentWidth = (int)Math.round(segmentWidthDefault * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
						break;
					default:
						/*
						 * Milliseconds
						 */
						segmentWidth = (int)Math.round(segmentWidthDefault);
						break;
				}
				/*
				 * Determine the segment width by the peak target.
				 */
				List<List<IScan>> stackedScans = new ArrayList<>();
				List<IPeak> peaks = getRecurringPeaks(chromatogram, recurringPeakName);
				//
				if(!peaks.isEmpty()) {
					/*
					 * Determine the optimal width
					 */
					int offsetLeft = Integer.MAX_VALUE;
					int offsetRight = Integer.MAX_VALUE;
					int max = peaks.size() - 1;
					Collections.sort(peaks, (p1, p2) -> Integer.compare(getRetentionTime(p1), getRetentionTime(p2)));
					int startRetentionTime = chromatogram.getStartRetentionTime();
					int stopRetentionTime = chromatogram.getStopRetentionTime();
					//
					for(int i = 0; i <= max; i++) {
						int retentionTime = getRetentionTime(peaks.get(i));
						if(i == 0) {
							IPeak peakNext = peaks.get(i + 1);
							offsetLeft = Math.min(retentionTime - startRetentionTime, offsetLeft);
							offsetRight = Math.min(getRetentionTime(peakNext) - retentionTime, offsetRight);
						} else if(i == max) {
							IPeak peakPrevious = peaks.get(i - 1);
							offsetLeft = Math.min(retentionTime - getRetentionTime(peakPrevious), offsetLeft);
							offsetRight = Math.min(stopRetentionTime - retentionTime, offsetRight);
						} else {
							IPeak peakPrevious = peaks.get(i - 1);
							IPeak peakNext = peaks.get(i + 1);
							offsetLeft = Math.min(retentionTime - getRetentionTime(peakPrevious), offsetLeft);
							offsetRight = Math.min(getRetentionTime(peakNext) - retentionTime, offsetRight);
						}
					}
					//
					int offset = Math.min(offsetLeft, offsetRight);
					for(IPeak peak : peaks) {
						List<IScan> scans = new ArrayList<>();
						int retentionTimePeak = getRetentionTime(peak);
						int retentionTimeLeft = retentionTimePeak - offset;
						int retentionTimeRight = retentionTimePeak + offset;
						for(IScan scan : chromatogram.getScans()) {
							int retentionTime = scan.getRetentionTime();
							if(retentionTime >= retentionTimeLeft && retentionTime <= retentionTimeRight) {
								/*
								 * Important, make a deep copy here
								 * due to possible overlapping ranges.
								 */
								IScan copy = copyScan(scan);
								if(copy != null) {
									scans.add(copy);
								}
							}
						}
						stackedScans.add(scans);
					}
				} else {
					/*
					 * Collect Normal
					 */
					if(chromatogram.getStopRetentionTime() > segmentWidth) {
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
					}
				}
				/*
				 * Assign
				 */
				if(!stackedScans.isEmpty()) {
					/*
					 * Master
					 */
					int scanInterval = chromatogram.getScanInterval();
					chromatogram.removeAllPeaks();
					chromatogram.replaceAllScans(stackedScans.get(0));
					ChromatogramSupport.assignIdentifier(chromatogram, headerField, "Cut 1");
					calculateScanIntervalAndDelay(chromatogram, resetRetentionTimes, 0, scanInterval);
					/*
					 * References
					 */
					for(int i = 1; i < stackedScans.size(); i++) {
						IChromatogram<?> chromatogramReference = createChromatogramReference(chromatogram);
						ChromatogramSupport.assignIdentifier(chromatogramReference, headerField, "Cut " + (i + 1));
						chromatogramReference.addScans(stackedScans.get(i));
						calculateScanIntervalAndDelay(chromatogramReference, resetRetentionTimes, 0, scanInterval);
						chromatogram.addReferencedChromatogram(chromatogramReference);
					}
				}
			}
		}
		//
		return processingInfo;
	}

	private IScan copyScan(IScan scan) {

		IScan copy = null;
		try {
			if(scan instanceof IScanCSD scanCSD) {
				copy = new ScanCSD(scanCSD.getTotalSignal());
				copy.setRetentionTime(scanCSD.getRetentionTime());
			} else if(scan instanceof IScanMSD scanMSD) {
				copy = scanMSD.makeDeepCopy();
			} else if(scan instanceof IScanVSD scanVSD) {
				IScanVSD copyVSD = new ScanVSD();
				copyVSD.setRetentionTime(scan.getRetentionTime());
				for(ISignalVSD signalVSD : scanVSD.getProcessedSignals()) {
					if(signalVSD instanceof ISignalInfrared signalInfrared) {
						copyVSD.getProcessedSignals().add(new SignalInfrared(signalInfrared.getWavenumber(), signalInfrared.getAbsorbance()));
					} else if(signalVSD instanceof ISignalRaman signalRaman) {
						copyVSD.getProcessedSignals().add(new SignalRaman(signalRaman.getWavenumber(), signalRaman.getScattering()));
					}
				}
				copy = copyVSD;
			} else if(scan instanceof IScanWSD scanWSD) {
				IScanWSD copyWSD = new ScanWSD();
				copyWSD.setRetentionTime(scan.getRetentionTime());
				for(IScanSignalWSD scanSignal : scanWSD.getScanSignals()) {
					copyWSD.addScanSignal(new ScanSignalWSD(scanSignal.getWavelength(), scanSignal.getAbsorbance()));
				}
				copy = copyWSD;
			}
		} catch(CloneNotSupportedException e) {
		}
		//
		return copy;
	}

	private int getRetentionTime(IPeak peak) {

		return peak.getPeakModel().getPeakMaximum().getRetentionTime();
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsReshape filterSettings = PreferenceSupplier.getFilterSettingsReshape();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private List<IPeak> getRecurringPeaks(IChromatogram<?> chromatogram, String recurringPeakName) {

		List<IPeak> peaks = new ArrayList<>();
		for(IPeak peak : chromatogram.getPeaks()) {
			IIdentificationTarget target = TargetSupport.getBestIdentificationTarget(peak);
			if(target != null) {
				if(target.getLibraryInformation().getName().equals(recurringPeakName)) {
					peaks.add(peak);
				}
			}
		}
		//
		return peaks;
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
		} else if(chromatogram instanceof IChromatogramVSD) {
			chromatogramReference = new ChromatogramVSD();
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