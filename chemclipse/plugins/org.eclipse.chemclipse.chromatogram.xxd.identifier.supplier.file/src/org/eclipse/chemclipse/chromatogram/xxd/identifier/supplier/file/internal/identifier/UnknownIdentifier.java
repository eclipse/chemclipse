/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.internal.identifier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.support.TargetBuilderCSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilderMSD;
import org.eclipse.chemclipse.chromatogram.wsd.identifier.support.TargetBuilderWSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.model.targets.TargetUnknownSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class UnknownIdentifier {

	public static final String IDENTIFIER = "Unknown Identifier";
	//
	private static final TargetBuilderMSD TARGETBUILDER_MSD = new TargetBuilderMSD();
	private static final TargetBuilderCSD TARGETBUILDER_CSD = new TargetBuilderCSD();
	private static final TargetBuilderWSD TARGETBUILDER_WSD = new TargetBuilderWSD();

	public void runIdentificationPeak(List<? extends IPeak> peaks, float limitMatchFactor, TargetUnknownSettings targetUnknownSettings) {

		for(IPeak peak : peaks) {
			if(LimitSupport.doIdentify(peak.getTargets(), limitMatchFactor)) {
				if(peak instanceof IPeakMSD) {
					TARGETBUILDER_MSD.setPeakTargetUnknown((IPeakMSD)peak, IDENTIFIER, targetUnknownSettings);
				} else if(peak instanceof IPeakCSD) {
					TARGETBUILDER_CSD.setPeakTargetUnknown((IPeakCSD)peak, IDENTIFIER, targetUnknownSettings);
				} else if(peak instanceof IPeakWSD) {
					TARGETBUILDER_WSD.setPeakTargetUnknown((IPeakWSD)peak, IDENTIFIER, targetUnknownSettings);
				}
			}
		}
	}

	public void runIdentificationScan(List<? extends IScan> spectraList, float limitMatchFactor, TargetUnknownSettings targetUnknownSettings) {

		for(IScan scan : spectraList) {
			if(LimitSupport.doIdentify(scan.getTargets(), limitMatchFactor)) {
				if(scan instanceof IScanMSD) {
					TARGETBUILDER_MSD.setMassSpectrumTargetUnknown((IScanMSD)scan, IDENTIFIER, targetUnknownSettings);
				} else if(scan instanceof IScanWSD) {
					TARGETBUILDER_WSD.setWaveSpectrumTargetUnknown((IScanWSD)scan, IDENTIFIER, targetUnknownSettings);
				}
			}
		}
	}
}
