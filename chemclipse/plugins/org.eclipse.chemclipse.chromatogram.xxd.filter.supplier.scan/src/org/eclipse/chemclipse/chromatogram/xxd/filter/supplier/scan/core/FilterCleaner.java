/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsCleaner;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/**
 * This filter removes empty scans.
 *
 */

public class FilterCleaner extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			try {
				applyChromatogramCleanerFilter(chromatogramSelection, monitor);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Chromatogram Cleaner", "Empty scans have been removed successfully."));
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Empty scans have been removed successfully."));
				chromatogramSelection.getChromatogram().setDirty(true);
			} catch(FilterException e) {
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
			}
		}
		chromatogramSelection.getChromatogram().setDirty(true);
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsCleaner filterSettings = PreferenceSupplier.getCleanerFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void applyChromatogramCleanerFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) throws FilterException {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		List<Integer> scansToRemove = new ArrayList<Integer>();
		/*
		 * Iterate through all selected scans and mark those to be removed.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan chromatogramScan = chromatogram.getScan(scan);
			if(chromatogramScan instanceof IScanMSD scanMSD) {
				/*
				 * MSD
				 */
				if(scanMSD.isEmpty()) {
					scansToRemove.add(scan);
				}
			} else if(chromatogramScan instanceof IScanCSD scanCSD) {
				/*
				 * CSD
				 */
				if(scanCSD.getTotalSignal() == 0) {
					scansToRemove.add(scan);
				}
			} else if(chromatogramScan instanceof IScanWSD scanWSD) {
				/*
				 * WSD
				 */
				if(scanWSD.getScanSignals().isEmpty()) {
					scansToRemove.add(scan);
				}
			}
		}
		/*
		 * Use a remove counter, because each time a scan will be removed, the chromatogram contains one scan less.
		 */
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Remove empty scans from chromatogram.", 100);
		try {
			int removeCounter = 0;
			for(Integer scan : scansToRemove) {
				scan -= removeCounter;
				chromatogram.removeScan(scan);
				removeCounter++;
			}
			subMonitor.worked(1);
		} finally {
			SubMonitor.done(subMonitor);
		}
		//
		chromatogram.recalculateScanNumbers();
		chromatogramSelection.reset();
	}
}