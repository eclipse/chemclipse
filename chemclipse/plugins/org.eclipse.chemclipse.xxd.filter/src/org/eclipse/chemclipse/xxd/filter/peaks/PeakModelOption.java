/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import org.eclipse.chemclipse.support.text.ILabel;

public enum PeakModelOption implements ILabel {

	STRICT("Strict (Inflection Points Available)"), //
	NON_STRICT("Non-Strict (Flexible Peak Shape)"); //

	private String label = "";

	private PeakModelOption(String label) {

		this.label = label;
	}

	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}