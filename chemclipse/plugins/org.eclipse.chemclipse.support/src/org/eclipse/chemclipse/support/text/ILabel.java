/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - String[] items support has been added
 *******************************************************************************/
package org.eclipse.chemclipse.support.text;

import java.util.Arrays;

public interface ILabel {

	String label();

	static String[] getItems(Enum<?>[] values) {

		return Arrays.stream(values).map(Enum::name).toList().toArray(new String[values.length]);
	}

	static String[][] getOptions(Enum<?>[] values) {

		String[][] elements = new String[values.length][2];
		//
		int counter = 0;
		for(Enum<?> value : values) {
			elements[counter][0] = value instanceof ILabel label ? label.label() : value.toString();
			elements[counter][1] = value.name();
			counter++;
		}
		//
		return elements;
	}
}
