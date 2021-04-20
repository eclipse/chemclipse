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
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.internal.identifier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.TargetUnknownSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumUnknownSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.PeakUnknownSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilder;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class UnknownIdentifier {

	public static final String IDENTIFIER = "Unknown Identifier";
	//
	private static final TargetBuilder TARGETBUILDER = new TargetBuilder();

	public void runIdentification(List<? extends IPeakMSD> peaks, PeakUnknownSettings settings) {

		float limitMatchFactor = settings.getLimitMatchFactor();
		float matchQuality = settings.getMatchQuality();
		//
		TargetUnknownSettings targetUnknownSettings = new TargetUnknownSettings();
		targetUnknownSettings.setTargetName(settings.getTargetName());
		targetUnknownSettings.setMatchQuality(matchQuality);
		targetUnknownSettings.setNumberMZ(settings.getNumberOfMZ());
		targetUnknownSettings.setIncludeIntensityPercent(settings.isIncludeIntensityPercent());
		targetUnknownSettings.setMarkerStart(settings.getMarkerStart());
		targetUnknownSettings.setMarkerStop(settings.getMarkerStop());
		targetUnknownSettings.setIncludeRetentionTime(settings.isIncludeRetentionTime());
		targetUnknownSettings.setIncludeRetentionIndex(settings.isIncludeRetentionIndex());
		//
		for(IPeakMSD peak : peaks) {
			if(LimitSupport.doIdentify(peak.getTargets(), limitMatchFactor)) {
				TARGETBUILDER.setPeakTargetUnknown(peak, IDENTIFIER, targetUnknownSettings);
			}
		}
	}

	public void runIdentification(List<IScanMSD> massSpectraList, MassSpectrumUnknownSettings settings) {

		float limitMatchFactor = settings.getLimitMatchFactor();
		float matchQuality = settings.getMatchQuality();
		//
		TargetUnknownSettings targetUnknownSettings = new TargetUnknownSettings();
		targetUnknownSettings.setTargetName(settings.getTargetName());
		targetUnknownSettings.setMatchQuality(matchQuality);
		targetUnknownSettings.setNumberMZ(settings.getNumberOfMZ());
		targetUnknownSettings.setIncludeIntensityPercent(settings.isIncludeIntensityPercent());
		targetUnknownSettings.setMarkerStart(settings.getMarkerStart());
		targetUnknownSettings.setMarkerStop(settings.getMarkerStop());
		targetUnknownSettings.setIncludeRetentionTime(settings.isIncludeRetentionTime());
		targetUnknownSettings.setIncludeRetentionIndex(settings.isIncludeRetentionIndex());
		//
		for(IScanMSD scan : massSpectraList) {
			if(LimitSupport.doIdentify(scan.getTargets(), limitMatchFactor)) {
				TARGETBUILDER.setMassSpectrumTargetUnknown(scan, IDENTIFIER, targetUnknownSettings);
			}
		}
	}
}
