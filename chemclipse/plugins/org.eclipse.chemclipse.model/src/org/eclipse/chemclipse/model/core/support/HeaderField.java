/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
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

public enum HeaderField {
	DEFAULT("Default"), //
	NAME("Name"), //
	DATA_NAME("Data Name"), //
	SAMPLE_GROUP("Sample Group"), //
	SHORT_INFO("Short Info");

	private String label = "";

	private HeaderField(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}

	public static String[][] getOptions() {

		HeaderField[] headerFields = values();
		String[][] elements = new String[headerFields.length][2];
		//
		int counter = 0;
		for(HeaderField headerField : headerFields) {
			elements[counter][0] = headerField.getLabel();
			elements[counter][1] = headerField.name();
			counter++;
		}
		//
		return elements;
	}
}
