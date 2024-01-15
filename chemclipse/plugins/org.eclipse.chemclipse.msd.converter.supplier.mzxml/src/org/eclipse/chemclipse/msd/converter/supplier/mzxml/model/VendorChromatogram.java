/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - metadata
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.model;

import org.eclipse.chemclipse.msd.model.core.AbstractChromatogramMSD;

public class VendorChromatogram extends AbstractChromatogramMSD implements IVendorChromatogram {

	private static final long serialVersionUID = 9120477404438842118L;

	@Override
	public String getName() {

		return extractNameFromFile("mzXMLChromatogram");
	}

	@Override
	public String getIonisation() {

		return getHeaderDataOrDefault("Ionisation", "");
	}

	@Override
	public void setIonisation(String ionisation) {

		putHeaderData("Ionisation", ionisation);
	}

	@Override
	public String getMassAnalyzer() {

		return getHeaderDataOrDefault("Mass Analyzer", "");
	}

	@Override
	public void setMassAnalyzer(String massAnalyzer) {

		putHeaderData("Mass Analyzer", massAnalyzer);
	}

	@Override
	public String getMassDetector() {

		return getHeaderDataOrDefault("MS Detector", "");
	}

	@Override
	public void setMassDetector(String massDetector) {

		putHeaderData("MS Detector", massDetector);
	}

	@Override
	public String getSoftware() {

		return getHeaderDataOrDefault("Acquisition Software", "");
	}

	@Override
	public void setSoftware(String software) {

		putHeaderData("Acquisition Software", software);
	}
}
