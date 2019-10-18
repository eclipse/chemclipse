/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.model;

public enum Threshold implements IThreshold {
	OFF(1), LOW(2), MEDIUM(3), HIGH(4);

	private int threshold;

	private Threshold(int threshold) {
		this.threshold = threshold;
	}

	public int getThreshold() {

		return threshold;
	}
}
