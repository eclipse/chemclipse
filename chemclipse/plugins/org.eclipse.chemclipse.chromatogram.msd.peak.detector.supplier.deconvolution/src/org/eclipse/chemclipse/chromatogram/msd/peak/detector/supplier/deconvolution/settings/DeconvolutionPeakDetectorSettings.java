/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.AbstractPeakDetectorMSDSettings;

public class DeconvolutionPeakDetectorSettings extends AbstractPeakDetectorMSDSettings implements IDeconvolutionPeakDetectorSettings {

	private Sensitivity sensitivity = INITIAL_SENSITIVITY;

	@Override
	public Sensitivity getSensitivity() {

		return sensitivity;
	}

	@Override
	public void setSensitivity(Sensitivity sensitivity) {

		if(sensitivity != null) {
			this.sensitivity = sensitivity;
		}
	}
}
