/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.List;
import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.IMeasurementInfo;

public interface IPlate extends IMeasurementInfo {

	String NAME = "name";
	String DATE = "Date";
	String NOISEBAND = "Noiseband";
	String THRESHOLD = "Threshold";

	void setActiveChannel(int activeChannel);

	void setActiveSubset(String activeSubset);

	IDetectionFormat getDetectionFormat();

	void setDetectionFormat(IDetectionFormat detectionFormat);

	List<IDetectionFormat> getDetectionFormats();

	TreeSet<IWell> getWells();

	IWell getWell(int id);

	String getName();

	List<String> getSampleSubsets();
}