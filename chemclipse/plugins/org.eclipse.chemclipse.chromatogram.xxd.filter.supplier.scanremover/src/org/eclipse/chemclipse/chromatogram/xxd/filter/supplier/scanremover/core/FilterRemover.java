/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings.FilterSettingsRemover;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings.ScanRemoverPattern;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class FilterRemover extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			try {
				if(chromatogramFilterSettings instanceof FilterSettingsRemover) {
					FilterSettingsRemover settings = (FilterSettingsRemover)chromatogramFilterSettings;
					ScanRemoverPattern scanRemoverPattern = new ScanRemoverPattern(settings.getScanRemoverPattern());
					applyScanRemoverFilter(chromatogramSelection, scanRemoverPattern, monitor);
					processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Scan Remover", "Scans have been removed successfully."));
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Scans have been removed successfully."));
					settings = (FilterSettingsRemover)chromatogramFilterSettings;
				}
			} catch(FilterException e) {
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsRemover filterSettings = PreferenceSupplier.getRemoverFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	/**
	 * Removes the given ions stored in the excludedIons
	 * instance from the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @throws FilterException
	 */
	private void applyScanRemoverFilter(IChromatogramSelection chromatogramSelection, ScanRemoverPattern scanRemoverPattern, IProgressMonitor monitor) throws FilterException {

		if(chromatogramSelection != null && scanRemoverPattern != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			List<Integer> scansToRemove = new ArrayList<Integer>();
			/*
			 * Iterate through all selected scans and mark those to be removed.
			 */
			for(int scan = startScan; scan <= stopScan; scan++) {
				if(scanRemoverPattern.remove()) {
					monitor.subTask("Remove scan from chromatogram: " + scan);
					scansToRemove.add(scan);
				}
			}
			/*
			 * Use a remove counter, because each time a scan will be removed, the chromatogram contains one scan less.
			 */
			int removeCounter = 0;
			for(Integer scan : scansToRemove) {
				scan -= removeCounter;
				chromatogram.removeScan(scan);
				removeCounter++;
			}
			//
			chromatogram.recalculateScanNumbers();
			chromatogramSelection.reset();
		}
	}
}
