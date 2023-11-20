/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core.support;

import org.eclipse.chemclipse.support.text.ILabel;

public enum HeaderField implements ILabel {

	DEFAULT("Default"), //
	NAME("Name"), //
	DATA_NAME("Data Name"), //
	SAMPLE_NAME("Sample Name"), //
	SAMPLE_GROUP("Sample Group"), //
	SHORT_INFO("Short Info"), //
	MISC_INFO("Misc Info");

	private String label = "";

	private HeaderField(String label) {

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