/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io;

public interface IFormat {

	String MZXML_V_200 = "mzXML_2.0";
	String MZXML_V_210 = "mzXML_2.1";
	String MZXML_V_220 = "mzXML_2.2";
	String MZXML_V_300 = "mzXML_3.0";
	String MZXML_V_310 = "mzXML_3.1";
	String MZXML_V_320 = "mzXML_3.2";
	String CHROMATOGRAM_VERSION_LATEST = MZXML_V_320;
}
