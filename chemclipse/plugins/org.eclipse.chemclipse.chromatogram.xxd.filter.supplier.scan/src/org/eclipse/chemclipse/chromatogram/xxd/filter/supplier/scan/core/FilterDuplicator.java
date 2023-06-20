/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsDuplicator;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class FilterDuplicator extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsDuplicator settings) {
				applyScanDuplicatorFilter(chromatogramSelection, settings);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Scan Duplicator", "Scans have been duplicated successfully."));
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Scans have been duplicated successfully."));
				chromatogramSelection.getChromatogram().setDirty(true);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsDuplicator filterSettings = PreferenceSupplier.getDuplicatorFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void applyScanDuplicatorFilter(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsDuplicator settings) {

		if(chromatogramSelection != null) {
			/*
			 * The complete range of the chromatogram must be processed.
			 */
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			int newScanInterval = (int)Math.round(chromatogram.getScanInterval() / 2.0d);
			/*
			 * Map the scans.
			 */
			TreeMap<Integer, IScan> scanMap = new TreeMap<>();
			for(IScan scan : chromatogram.getScans()) {
				scanMap.put(scan.getRetentionTime(), scan);
			}
			//
			List<IScan> scansToReplace = new ArrayList<>();
			for(IScan scan : chromatogram.getScans()) {
				/*
				 * Add each scan.
				 */
				scansToReplace.add(scan);
				/*
				 * Duplicate the scan in the center of the current and the next scan.
				 */
				Map.Entry<Integer, IScan> scanEntryNext = scanMap.higherEntry(scan.getRetentionTime());
				if(scanEntryNext != null) {
					/*
					 * Next
					 */
					IScan scanNext = scanEntryNext.getValue();
					int retentionTimeCenter = (int)((scanNext.getRetentionTime() + scan.getRetentionTime()) / 2.0d);
					float intensityCenter = (scanNext.getTotalSignal() + scan.getTotalSignal()) / 2.0f;
					//
					if(scan instanceof IScanCSD) {
						/*
						 * CSD
						 */
						IScanCSD centerScan = new ScanCSD(intensityCenter);
						centerScan.setRetentionTime(retentionTimeCenter);
						scansToReplace.add(centerScan);
					} else if(scan instanceof IScanMSD currentScan) {
						if(scanNext instanceof IScanMSD nextScan) {
							/*
							 * MSD
							 */
							IScanMSD centerScan = new VendorMassSpectrum();
							centerScan.setRetentionTime(retentionTimeCenter);
							List<IIon> traces = new ArrayList<>();
							if(settings.isMergeScans()) {
								traces.addAll(createTraces(currentScan));
							}
							traces.addAll(createTraces(nextScan));
							centerScan.addIons(traces, true);
							centerScan.adjustTotalSignal(intensityCenter);
							scansToReplace.add(centerScan);
						}
					} else if(scan instanceof IScanWSD currentScan) {
						if(scanNext instanceof IScanWSD nextScan) {
							/*
							 * WSD
							 */
							IScanWSD centerScan = new ScanWSD();
							centerScan.setRetentionTime(retentionTimeCenter);
							Map<Float, Float> traceMap = new HashMap<>();
							traceMap.putAll(getTraces(nextScan));
							if(settings.isMergeScans()) {
								for(Map.Entry<Float, Float> entry : getTraces(currentScan).entrySet()) {
									Float abundance = traceMap.get(entry.getKey());
									if(abundance == null) {
										traceMap.put(entry.getKey(), entry.getValue());
									} else {
										traceMap.put(entry.getKey(), abundance + entry.getValue());
									}
								}
							}
							for(Map.Entry<Float, Float> entry : traceMap.entrySet()) {
								centerScan.addScanSignal(new ScanSignalWSD(entry.getKey(), entry.getValue()));
							}
							centerScan.adjustTotalSignal(intensityCenter);
							scansToReplace.add(centerScan);
						}
					}
				}
			}
			/*
			 * Don't recalculate the retention times
			 * chromatogram.recalculateRetentionTimes();
			 */
			chromatogram.replaceAllScans(scansToReplace);
			chromatogram.setScanDelay(chromatogram.getStartRetentionTime());
			chromatogram.setScanInterval(newScanInterval);
			chromatogram.recalculateScanNumbers();
			chromatogram.recalculateTheNoiseFactor();
			chromatogramSelection.reset();
		}
	}

	private List<IIon> createTraces(IScanMSD scanMSD) {

		List<IIon> traces = new ArrayList<>();
		for(IIon trace : scanMSD.getIons()) {
			try {
				traces.add(new Ion(trace.getIon(), trace.getAbundance()));
			} catch(Exception e) {
				// logger.warn(e);
			}
		}
		//
		return traces;
	}

	private Map<Float, Float> getTraces(IScanWSD scanWSD) {

		Map<Float, Float> traceMap = new HashMap<>();
		for(IScanSignalWSD trace : scanWSD.getScanSignals()) {
			traceMap.put(trace.getWavelength(), trace.getAbundance());
		}
		//
		return traceMap;
	}
}
