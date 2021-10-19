/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core.support;

import org.eclipse.chemclipse.support.text.ILabel;

public enum LabelSetting implements ILabel {
	SAMPLENAME("Sample ID"), //
	COORDINATE("Coordinate"), //
	COORDINATE_SAMPLENAME("Coordinate + Sample ID"); //

	private String label = "";

	private LabelSetting(String label) {

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