/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

public class PreferencesUtils {

	public static String[][] enumToArray(Class<? extends Enum> enums) {

		Enum[] enumValues = enums.getEnumConstants();
		String[][] convertValues = new String[enumValues.length][2];
		for(int i = 0; i < enumValues.length; i++) {
			convertValues[i][0] = enumValues[i].toString();
			convertValues[i][1] = enumValues[i].name();
		}
		return convertValues;
	}
}
