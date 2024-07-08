/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.versions;

public interface IFormatVersion {

	String getVersion();

	String getRelease();

	default String getLabel() {

		return getVersion() + " (" + getRelease() + ")";
	}

	static String[][] getOptions(IFormatVersion[] values) {

		String[][] elements = new String[values.length][2];
		//
		int counter = 0;
		for(IFormatVersion value : values) {
			elements[counter][0] = value.getLabel();
			elements[counter][1] = value.getVersion();
			counter++;
		}
		//
		return elements;
	}
}
