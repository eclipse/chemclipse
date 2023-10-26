/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;

/*
 * Use for backward compatibility <= v1500 ("McLafferty")
 */
public class RetentionIndexTypeSupport {

	/*
	 * public enum RetentionIndexType {
	 * POLAR, //
	 * SEMIPOLAR, //
	 * APOLAR;
	 * }
	 */
	public static String getBackwardCompatibleName(SeparationColumnType separationColumnType) {

		String name = "POLAR";
		if(separationColumnType != null) {
			switch(separationColumnType) {
				case NON_POLAR:
					name = "APOLAR";
					break;
				case SEMI_POLAR:
					name = "SEMIPOLAR";
					break;
				default:
					break;
			}
		}
		//
		return name;
	}
}