/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.model;

import java.util.Map;

import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

public interface IVendorLibraryMassSpectrum extends IRegularLibraryMassSpectrum {

	String PEAK_MARKER = "PK$";
	String PEAK_MARKER_AMOUNT = "PK$NUM_PEAK";
	String PEAK_MARKER_STOP = "//";
	String DELIMITER_MZ = " ";
	//
	String DELIMITER_DESCRIPTION_DATA = ": ";
	String COMMENT = "COMMENT";
	String NAME = "CH$NAME";
	String COMPOUND_CLASS = "CH$COMPOUND_CLASS";
	String FORMULA = "CH$FORMULA";
	String EXACT_MASS = "CH$EXACT_MASS";
	String SMILES = "CH$SMILES";

	/**
	 * The info map stores all description data.
	 * 
	 * @return {@link Map}
	 */
	Map<String, String> getInfoMap();
}
