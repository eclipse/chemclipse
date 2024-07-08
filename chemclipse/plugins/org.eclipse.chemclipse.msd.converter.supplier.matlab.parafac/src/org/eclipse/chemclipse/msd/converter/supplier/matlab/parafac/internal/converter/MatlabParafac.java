/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.internal.converter;

public class MatlabParafac {

	private MatlabParafac() {

	}

	public static final String PEAK_IDENTIFIER = "#------------------------------------------";
	public static final String COMMENT = "#";
	public static final String DESCRIPTION = "description";
	public static final String MASS_SPECTRUM = "# mass spectrum";
	public static final String MASS_SPECTRUM_INFO = "(m/z - intensity)";
	public static final String ELUTION_PROFILE = "# elution profile";
	public static final String ELUTION_PROFILE_INFO = "(minutes converted to milliseconds - intensity)";
	public static final String VALUE_DELIMITER = "\t";
	public static final String CRLF = "\r\n";
	public static final String LF = "\n";
	public static final String CR = "\r";
}
