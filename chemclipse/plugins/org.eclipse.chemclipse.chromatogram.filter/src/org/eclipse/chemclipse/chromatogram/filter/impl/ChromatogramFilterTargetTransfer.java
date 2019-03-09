/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsTargetTransfer;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterTargetTransfer extends AbstractChromatogramFilter implements IChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsTargetTransfer) {
				FilterSettingsTargetTransfer filterSettings = (FilterSettingsTargetTransfer)chromatogramFilterSettings;
				transferScanTargets(chromatogramSelection, filterSettings);
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Targets transfered successfully."));
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsTargetTransfer filterSettings = PreferenceSupplier.getFilterSettingsTargetTransfer();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	@SuppressWarnings("unchecked")
	private void transferScanTargets(IChromatogramSelection chromatogramSelection, FilterSettingsTargetTransfer filterSettings) {

		List<? extends IPeak> peaks = chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
		List<IScan> identifiedScans = extractIdentifiedScans(chromatogramSelection);
		//
		for(IPeak peak : peaks) {
			List<IScan> targetScans = getScansInPeakRange(peak, identifiedScans, filterSettings);
			for(IScan targetScan : targetScans) {
				for(IIdentificationTarget sourceTarget : targetScan.getTargets()) {
					ILibraryInformation libraryInformation = new LibraryInformation(sourceTarget.getLibraryInformation());
					IComparisonResult comparisonResult = new ComparisonResult(sourceTarget.getComparisonResult());
					IdentificationTarget sinkTarget = new IdentificationTarget(libraryInformation, comparisonResult);
					peak.getTargets().add(sinkTarget);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<IScan> extractIdentifiedScans(IChromatogramSelection chromatogramSelection) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		//
		List<IScan> identifiedScans = new ArrayList<>();
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			if(scan.getTargets().size() > 0) {
				identifiedScans.add(scan);
			}
		}
		//
		return identifiedScans;
	}

	private List<IScan> getScansInPeakRange(IPeak peak, List<IScan> scans, FilterSettingsTargetTransfer filterSettings) {

		List<IScan> targetScans = new ArrayList<>();
		//
		IPeakModel peakModel = peak.getPeakModel();
		int startRetentionTime = peakModel.getStartRetentionTime();
		int stopRetentionTime = peakModel.getStopRetentionTime();
		int peakMaxRetentionTime = peakModel.getRetentionTimeAtPeakMaximum();
		boolean transferClosestScan = filterSettings.isTransferClosestScan();
		//
		for(IScan scan : scans) {
			int retentionTime = scan.getRetentionTime();
			if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
				/*
				 * Transfer all or only the closest.
				 */
				if(!transferClosestScan) {
					targetScans.add(scan);
				} else {
					if(targetScans.isEmpty()) {
						targetScans.add(scan);
					} else {
						int delta = Math.abs(peakMaxRetentionTime - scan.getRetentionTime());
						IScan scanClosest = targetScans.get(0);
						int deltaClosest = Math.abs(peakMaxRetentionTime - scanClosest.getRetentionTime());
						//
						if(delta < deltaClosest) {
							targetScans.clear();
							targetScans.add(scan);
						}
					}
				}
			}
		}
		//
		return targetScans;
	}
}
