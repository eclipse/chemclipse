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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;

public interface IAmdisSettings extends IPeakDetectorSettingsMSD {

	IOnsiteSettings getOnsiteSettings();

	float getMinSignalToNoiseRatio();

	void setMinSignalToNoiseRatio(float minSignalToNoiseRatio);

	float getMinLeading();

	void setMinLeading(float minLeading);

	float getMaxLeading();

	void setMaxLeading(float maxLeading);

	float getMinTailing();

	void setMinTailing(float minTailing);

	float getMaxTailing();

	void setMaxTailing(float maxTailing);
}
