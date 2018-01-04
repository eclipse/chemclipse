/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

public enum Sensitivity implements ISensitivity {
	OFF(1), LOW(2), MEDIUM(3), HIGH(4);

	private int sensitivity;

	private Sensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}

	@Override
	public int getSensitivity() {

		return sensitivity;
	}
}
