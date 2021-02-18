/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.List;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class TargetTransferSupport {

	/**
	 * Returns a message if something went wrong.
	 * Null if everything is ok.
	 * 
	 * 
	 * @param peaksSource
	 * @param peaksSink
	 * @param retentionTimeDelta
	 * @param useBestTargetOnly
	 * @return
	 */
	public String transferPeakTargets(List<? extends IPeak> peaksSource, List<? extends IPeak> peaksSink, int retentionTimeDelta, boolean useBestTargetOnly) {

		if(peaksSource.size() > 0) {
			if(peaksSink.size() > 0) {
				for(IPeak peakSink : peaksSink) {
					for(IPeak peakSource : peaksSource) {
						int retentionTimePeakSink = peakSink.getPeakModel().getRetentionTimeAtPeakMaximum();
						int retentionTimePeakSource = peakSource.getPeakModel().getRetentionTimeAtPeakMaximum();
						if(isPeakInFocus(retentionTimePeakSink, retentionTimePeakSource, retentionTimeDelta)) {
							/*
							 * Best target or all?
							 */
							if(useBestTargetOnly) {
								float retentionIndex = peakSource.getPeakModel().getPeakMaximum().getRetentionIndex();
								IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
								IIdentificationTarget peakTarget = IIdentificationTarget.getBestIdentificationTarget(peakSource.getTargets(), identificationTargetComparator);
								transferPeakTarget(peakTarget, peakSink);
							} else {
								for(IIdentificationTarget peakTarget : peakSource.getTargets()) {
									transferPeakTarget(peakTarget, peakSink);
								}
							}
						}
					}
				}
			} else {
				return "The sink chromatogram contains no peaks.";
			}
		} else {
			return "The source chromatogram contains no peaks.";
		}
		//
		return null;
	}

	@SuppressWarnings("rawtypes")
	public String transferScanTargets(List<IScan> scansSource, IChromatogram chromatogramSink, boolean useBestTargetOnly) {

		if(scansSource.size() > 0) {
			for(IScan scanSource : scansSource) {
				int scanNumber = chromatogramSink.getScanNumber(scanSource.getRetentionTime());
				if(scanNumber > 0) {
					IScan scanSink = chromatogramSink.getScan(scanNumber);
					if(scanSink != null) {
						if(useBestTargetOnly) {
							float retentionIndex = scanSource.getRetentionIndex();
							IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
							IIdentificationTarget peakTarget = IIdentificationTarget.getBestIdentificationTarget(scanSource.getTargets(), identificationTargetComparator);
							transferScanTarget(peakTarget, scanSink);
						} else {
							for(IIdentificationTarget scanTarget : scanSource.getTargets()) {
								transferScanTarget(scanTarget, scanSink);
							}
						}
					}
				}
			}
		} else {
			return "The source chromatogram contains no identified scans.";
		}
		//
		return null;
	}

	private boolean isPeakInFocus(int retentionTimePeakSink, int retentionTimePeakSource, int retentionTimeDelta) {

		if(retentionTimePeakSink >= (retentionTimePeakSource - retentionTimeDelta) && retentionTimePeakSink <= (retentionTimePeakSource + retentionTimeDelta)) {
			return true;
		} else {
			return false;
		}
	}

	private void transferPeakTarget(IIdentificationTarget identificationTargetSource, IPeak peakSink) {

		peakSink.getTargets().add(copyIdentificationTarget(identificationTargetSource));
	}

	private void transferScanTarget(IIdentificationTarget identificationTargetSource, IScan scanSink) {

		scanSink.getTargets().add(copyIdentificationTarget(identificationTargetSource));
	}

	private IIdentificationTarget copyIdentificationTarget(IIdentificationTarget identificationTargetSource) {

		ILibraryInformation libraryInformation = new LibraryInformation(identificationTargetSource.getLibraryInformation());
		IComparisonResult comparisonResult = new ComparisonResult(identificationTargetSource.getComparisonResult());
		return new IdentificationTarget(libraryInformation, comparisonResult);
	}
}
