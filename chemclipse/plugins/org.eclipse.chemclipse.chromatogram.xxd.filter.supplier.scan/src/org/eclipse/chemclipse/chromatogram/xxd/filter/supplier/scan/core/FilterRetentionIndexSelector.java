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

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsRetentionIndexSelector;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.ColumnIndexSupport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;


public class FilterRetentionIndexSelector extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsRetentionIndexSelector settings) {
				selectRetentionIndices(chromatogramSelection, settings);
				processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Select Retention Index", "The retention indices have been selected successfully."));
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Retention Index selection was successful."));
				chromatogramSelection.getChromatogram().setDirty(true);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsRetentionIndexSelector filterSettings = PreferenceSupplier.getFilterSettingsRetentionIndexSelector();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void selectRetentionIndices(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsRetentionIndexSelector settings) {

		String searchColumn = settings.getSearchColumn();
		if(!searchColumn.isEmpty()) {
			/*
			 * Settings
			 */
			boolean caseSensitive = settings.isCaseSensitive();
			boolean removeWhiteSpace = settings.isRemoveWhiteSpace();
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(FilterSettingsRetentionIndexSelector.CHROMATOGRAM_COLUMN_TYPE.equals(searchColumn)) {
				searchColumn = chromatogram.getSeparationColumnIndices().getSeparationColumn().getSeparationColumnType().label();
			}
			/*
			 * Scans
			 */
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			for(int i = startScan; i <= stopScan; i++) {
				IScan scan = chromatogram.getScan(i);
				if(!scan.getTargets().isEmpty()) {
					selectColumnRetentionIndex(scan.getTargets(), searchColumn, caseSensitive, removeWhiteSpace);
				}
			}
			/*
			 * Peaks
			 */
			List<? extends IPeak> peaks = chromatogram.getPeaks(chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			for(IPeak peak : peaks) {
				selectColumnRetentionIndex(peak.getTargets(), searchColumn, caseSensitive, removeWhiteSpace);
			}
		}
	}

	private void selectColumnRetentionIndex(Set<IIdentificationTarget> identificationTargets, String searchColumn, boolean caseSensitive, boolean removeWhiteSpace) {

		for(IIdentificationTarget identificationTarget : identificationTargets) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			float retentionIndex = ColumnIndexSupport.getRetentionIndex(libraryInformation.getColumnIndexMarkers(), searchColumn, caseSensitive, removeWhiteSpace);
			libraryInformation.setRetentionIndex(retentionIndex);
		}
	}
}