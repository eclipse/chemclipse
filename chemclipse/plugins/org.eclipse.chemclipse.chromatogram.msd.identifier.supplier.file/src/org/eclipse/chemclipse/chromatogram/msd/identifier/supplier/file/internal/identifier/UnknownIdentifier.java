/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumUnknownSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.PeakUnknownSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilder;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class UnknownIdentifier {

	public static final String IDENTIFIER = "Unknown Identifier";
	//
	private static final TargetBuilder TARGETBUILDER = new TargetBuilder();

	public void runIdentification(List<? extends IPeakMSD> peaks, PeakUnknownSettings settings) {

		float minMatchFactor = settings.getMinMatchFactor();
		float minReverseMatchFactor = settings.getMinReverseMatchFactor();
		//
		for(IPeakMSD peak : peaks) {
			if(identifyPeak(peak, minMatchFactor, minReverseMatchFactor)) {
				TARGETBUILDER.setPeakTargetUnknown(peak, IDENTIFIER);
			}
		}
	}

	public void runIdentification(List<IScanMSD> massSpectraList, MassSpectrumUnknownSettings settings) {

		float minMatchFactor = settings.getMinMatchFactor();
		float minReverseMatchFactor = settings.getMinReverseMatchFactor();
		//
		for(IScanMSD scan : massSpectraList) {
			if(identifyScan(scan, minMatchFactor, minReverseMatchFactor)) {
				TARGETBUILDER.setMassSpectrumTargetUnknown(scan, IDENTIFIER);
			}
		}
	}

	private boolean identifyPeak(IPeak peak, float minMatchFactor, float minReverseMatchFactor) {

		for(IIdentificationTarget target : peak.getTargets()) {
			IComparisonResult comparisonResult = target.getComparisonResult();
			if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
				return false;
			}
		}
		return true;
	}

	private boolean identifyScan(IScanMSD scan, float minMatchFactor, float minReverseMatchFactor) {

		for(IIdentificationTarget target : scan.getTargets()) {
			IComparisonResult comparisonResult = target.getComparisonResult();
			if(comparisonResult.getMatchFactor() >= minMatchFactor && comparisonResult.getReverseMatchFactor() >= minReverseMatchFactor) {
				return false;
			}
		}
		return true;
	}
}
