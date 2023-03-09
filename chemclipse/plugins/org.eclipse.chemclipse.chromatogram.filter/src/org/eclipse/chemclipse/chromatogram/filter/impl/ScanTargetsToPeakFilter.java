/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.ScanTargetsToPeakSettings;
import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
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
public class ScanTargetsToPeakFilter extends AbstractTransferFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof ScanTargetsToPeakSettings settings) {
				transferTargets(chromatogramSelection, settings);
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, Messages.targetsTransferredSuccessfully));
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		ScanTargetsToPeakSettings settings = PreferenceSupplier.getScanToPeakTargetTransferSettings();
		return applyFilter(chromatogramSelection, settings, monitor);
	}

	private void transferTargets(IChromatogramSelection chromatogramSelection, ScanTargetsToPeakSettings settings) {

		List<IScan> identifiedScans = extractIdentifiedScans(chromatogramSelection);
		List<? extends IPeak> peaks = extractPeaks(chromatogramSelection);
		//
		for(IPeak peak : peaks) {
			List<IScan> targetScans = getScansInPeakRange(peak, identifiedScans, settings);
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

	private List<IScan> getScansInPeakRange(IPeak peak, List<IScan> scans, ScanTargetsToPeakSettings settings) {

		List<IScan> targetScans = new ArrayList<>();
		//
		IPeakModel peakModel = peak.getPeakModel();
		int startRetentionTime = peakModel.getStartRetentionTime();
		int stopRetentionTime = peakModel.getStopRetentionTime();
		int peakMaxRetentionTime = peakModel.getRetentionTimeAtPeakMaximum();
		boolean transferClosestScan = settings.isTransferClosestScan();
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
