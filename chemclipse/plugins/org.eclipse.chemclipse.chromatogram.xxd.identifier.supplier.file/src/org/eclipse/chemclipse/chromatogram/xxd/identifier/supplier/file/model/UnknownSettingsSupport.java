/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.model;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IUnknownSettings;
import org.eclipse.chemclipse.model.targets.TargetUnknownSettings;

public class UnknownSettingsSupport {

	public static TargetUnknownSettings getTargetUnknownSettings(IUnknownSettings unknownSettings) {

		TargetUnknownSettings targetUnknownSettings = new TargetUnknownSettings();
		//
		targetUnknownSettings.setTargetName(unknownSettings.getTargetName());
		targetUnknownSettings.setMatchQuality(unknownSettings.getMatchQuality());
		targetUnknownSettings.setNumberTraces(unknownSettings.getNumberOfTraces());
		targetUnknownSettings.setIncludeIntensityPercent(unknownSettings.isIncludeIntensityPercent());
		targetUnknownSettings.setMarkerStart(unknownSettings.getMarkerStart());
		targetUnknownSettings.setMarkerStop(unknownSettings.getMarkerStop());
		targetUnknownSettings.setIncludeRetentionTime(unknownSettings.isIncludeRetentionTime());
		targetUnknownSettings.setIncludeRetentionIndex(unknownSettings.isIncludeRetentionIndex());
		//
		return targetUnknownSettings;
	}
}