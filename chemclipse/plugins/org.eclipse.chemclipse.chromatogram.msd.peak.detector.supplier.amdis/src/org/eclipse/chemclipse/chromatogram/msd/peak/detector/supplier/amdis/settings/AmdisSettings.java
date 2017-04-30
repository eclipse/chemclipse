/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorMSDSettings;

public class AmdisSettings extends AbstractPeakDetectorMSDSettings implements IAmdisSettings {

	private IOnsiteSettings onsiteSettings;
	private float minSignalToNoiseRatio;
	private float minLeading;
	private float maxLeading;
	private float minTailing;
	private float maxTailing;

	public AmdisSettings() {
		onsiteSettings = new OnsiteSettings();
	}

	@Override
	public IOnsiteSettings getOnsiteSettings() {

		return onsiteSettings;
	}

	@Override
	public float getMinSignalToNoiseRatio() {

		return minSignalToNoiseRatio;
	}

	@Override
	public void setMinSignalToNoiseRatio(float minSignalToNoiseRatio) {

		this.minSignalToNoiseRatio = minSignalToNoiseRatio;
	}

	@Override
	public float getMinLeading() {

		return minLeading;
	}

	@Override
	public void setMinLeading(float minLeading) {

		this.minLeading = minLeading;
	}

	@Override
	public float getMaxLeading() {

		return maxLeading;
	}

	@Override
	public void setMaxLeading(float maxLeading) {

		this.maxLeading = maxLeading;
	}

	@Override
	public float getMinTailing() {

		return minTailing;
	}

	@Override
	public void setMinTailing(float minTailing) {

		this.minTailing = minTailing;
	}

	@Override
	public float getMaxTailing() {

		return maxTailing;
	}

	@Override
	public void setMaxTailing(float maxTailing) {

		this.maxTailing = maxTailing;
	}
}