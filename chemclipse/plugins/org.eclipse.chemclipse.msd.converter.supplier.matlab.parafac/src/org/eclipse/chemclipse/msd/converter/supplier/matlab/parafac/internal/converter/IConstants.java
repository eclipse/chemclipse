/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

public interface IConstants {

	String PROCESS_INFO_DESCRIPTION = "Import Peaks";
	String PEAK_IDENTIFIER = "#------------------------------------------";
	String COMMENT = "#";
	String DESCRIPTION = "description";
	String MASS_SPECTRUM = "# mass spectrum";
	String MASS_SPECTRUM_INFO = "(m/z - intensity)";
	String ELUTION_PROFILE = "# elution profile";
	String ELUTION_PROFILE_INFO = "(minutes converted to milliseconds - intensity)";
	String VALUE_DELIMITER = "\t";
	String CRLF = "\r\n";
	String LF = "\n";
	String CR = "\r";
}
