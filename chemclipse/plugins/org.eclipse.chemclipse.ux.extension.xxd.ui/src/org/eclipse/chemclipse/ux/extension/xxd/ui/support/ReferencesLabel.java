/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

public enum ReferencesLabel {
	DEFAULT("Default"), //
	NAME("Name"), //
	DATA_NAME("Data Name"), //
	SAMPLE_GROUP("Sample Group"), //
	SHORT_INFO("Short Info");

	private String label = "";

	private ReferencesLabel(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}
}
