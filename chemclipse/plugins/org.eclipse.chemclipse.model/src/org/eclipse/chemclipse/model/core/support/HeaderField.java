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

		return getOptions(values());
	}

	public static String[][] getOptions(HeaderField[] elements) {

		String[][] dataArray = new String[elements.length][2];
		//
		int counter = 0;
		for(HeaderField element : elements) {
			dataArray[counter][0] = element.getLabel();
			dataArray[counter][1] = element.name();
			counter++;
		}
		//
		return dataArray;
	}
}