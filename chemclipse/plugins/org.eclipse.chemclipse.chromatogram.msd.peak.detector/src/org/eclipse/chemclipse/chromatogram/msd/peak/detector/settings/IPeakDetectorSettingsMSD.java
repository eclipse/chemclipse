/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings;

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

public interface IPeakDetectorSettingsMSD extends IPeakDetectorSettings {

	enum FilterMode {
		INCLUDE, EXCLUDE
	}

	float getMinimumSignalToNoiseRatio();

	boolean isIncludeBackground();

	WindowSize getMovingAverageWindowSize();

	Collection<Number> getFilterIon();

	FilterMode getFilterMode();

}
