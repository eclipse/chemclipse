/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add label
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.model;

import org.eclipse.chemclipse.support.text.ILabel;

public enum Threshold implements ILabel {
	OFF(1), //
	LOW(2), //
	MEDIUM(3), //
	HIGH(4);

	private int threshold;

	private Threshold(int threshold) {

		this.threshold = threshold;
	}

	public int getThreshold() {

		return threshold;
	}

	@Override
	public String label() {

		return name().toLowerCase();
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}