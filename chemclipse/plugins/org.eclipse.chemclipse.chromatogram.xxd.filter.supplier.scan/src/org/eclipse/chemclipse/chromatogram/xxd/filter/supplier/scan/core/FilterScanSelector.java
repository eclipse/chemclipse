/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsScanSelector;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.RetentionIndexMap;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class FilterScanSelector extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			try {
				if(chromatogramFilterSettings instanceof FilterSettingsScanSelector) {
					FilterSettingsScanSelector settings = (FilterSettingsScanSelector)chromatogramFilterSettings;
					selectScan(chromatogramSelection, settings, monitor);
					processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Select Scan", "The scan has been selected successfully."));
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Scan selection was successful."));
					chromatogramSelection.getChromatogram().setDirty(true);
				}
			} catch(FilterException e) {
				processingInfo.addMessage(new ProcessingMessage(MessageType.WARN, "Select Scan", e.getMessage()));
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsScanSelector filterSettings = PreferenceSupplier.getFilterSettingsScanSelector();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void selectScan(IChromatogramSelection chromatogramSelection, FilterSettingsScanSelector filterSettingsScanSelector, IProgressMonitor monitor) throws FilterException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		int scanNumber = getScanNumber(chromatogram, filterSettingsScanSelector);
		//
		if(scanNumber >= startScan && scanNumber <= stopScan) {
			IScan scan = chromatogram.getScan(scanNumber);
			chromatogramSelection.setSelectedScan(scan);
		} else {
			throw new FilterException("The scan is outside of the chromatogram selection range.");
		}
	}

	private int getScanNumber(IChromatogram chromatogram, FilterSettingsScanSelector filterSettingsScanSelector) {

		int scanNumber;
		double value = filterSettingsScanSelector.getScanSelectorValue();
		switch(filterSettingsScanSelector.getScanSelectorOption()) {
			case SCAN_NUMER:
				scanNumber = (int)Math.round(value);
				break;
			case RETENTION_TIME_MS:
				scanNumber = chromatogram.getScanNumber((int)Math.round(value));
				break;
			case RETENTION_TIME_MIN:
				scanNumber = chromatogram.getScanNumber((int)Math.round(value * IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				break;
			case RETENTION_INDEX:
				RetentionIndexMap retentionIndexMap = new RetentionIndexMap();
				retentionIndexMap.updateRetentionIndexMap(chromatogram);
				int retentionTime = retentionIndexMap.getRetentionTime((int)Math.round(value));
				if(retentionTime > -1) {
					scanNumber = chromatogram.getScanNumber(retentionTime);
				} else {
					scanNumber = -1;
				}
				break;
			default:
				scanNumber = -1;
				break;
		}
		//
		return scanNumber;
	}
}