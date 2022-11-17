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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsRetentionIndexSelector;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class FilterRetentionIndexSelector extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			try {
				if(chromatogramFilterSettings instanceof FilterSettingsRetentionIndexSelector settings) {
					selectRetentionIndices(chromatogramSelection, settings, monitor);
					processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Select Retention Index", "The retention indices have been selected successfully."));
					processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Retention Index selection was successful."));
					chromatogramSelection.getChromatogram().setDirty(true);
				}
			} catch(FilterException e) {
				processingInfo.addMessage(new ProcessingMessage(MessageType.WARN, "Select Retention Index", e.getMessage()));
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage()));
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsRetentionIndexSelector filterSettings = PreferenceSupplier.getFilterSettingsRetentionIndexSelector();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void selectRetentionIndices(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsRetentionIndexSelector settings, IProgressMonitor monitor) throws FilterException {

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
			searchColumn = adjustValue(searchColumn, caseSensitive, removeWhiteSpace);
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
			float retentionIndex = getRetentionIndex(libraryInformation.getColumnIndexMarkers(), searchColumn, caseSensitive, removeWhiteSpace);
			libraryInformation.setRetentionIndex(retentionIndex);
		}
	}

	private float getRetentionIndex(List<IColumnIndexMarker> columnIndexMarkers, String searchColumn, boolean caseSensitive, boolean removeWhiteSpace) {

		float retentionIndex = 0.0f;
		List<IColumnIndexMarker> columnIndexMarkersSorted = new ArrayList<>(columnIndexMarkers);
		Collections.sort(columnIndexMarkersSorted, (c1, c2) -> Float.compare(c1.getRetentionIndex(), c2.getRetentionIndex()));
		//
		for(IColumnIndexMarker columnIndexMarker : columnIndexMarkers) {
			ISeparationColumn separationColumn = columnIndexMarker.getSeparationColumn();
			String separationColumnType = adjustValue(separationColumn.getSeparationColumnType().label(), caseSensitive, removeWhiteSpace);
			String name = adjustValue(separationColumn.getName(), caseSensitive, removeWhiteSpace);
			//
			if(separationColumnType.contains(searchColumn) || name.contains(searchColumn)) {
				retentionIndex = columnIndexMarker.getRetentionIndex();
			}
		}
		//
		return retentionIndex;
	}

	private String adjustValue(String value, boolean caseSensitive, boolean removeWhiteSpace) {

		value = removeWhiteSpace ? value.replace(" ", "") : value;
		return caseSensitive ? value : value.toLowerCase();
	}
}