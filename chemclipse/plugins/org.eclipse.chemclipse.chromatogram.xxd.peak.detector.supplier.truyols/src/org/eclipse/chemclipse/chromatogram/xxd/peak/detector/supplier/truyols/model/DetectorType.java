/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.model;

import org.eclipse.chemclipse.support.text.ILabel;

public enum DetectorType implements ILabel {

	BB("BB (Baseline)"), //
	VV("VV (Valley)"), //
	CB("CB (Chromatogram Baseline)");

	private String label = "";

	private DetectorType(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}