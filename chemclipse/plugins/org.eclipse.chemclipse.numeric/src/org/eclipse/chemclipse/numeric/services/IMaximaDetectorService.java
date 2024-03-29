/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.services;

public interface IMaximaDetectorService {

	String getId();

	String getName();

	String getDescription();

	String getVersion();

	IMaximaDetectorSettings getSettings();

	/**
	 * Calculates the x position maxima, based on given x and y values.
	 * 
	 * @param xValues
	 * @param yValues
	 * @param maximaDetectorSettings
	 * @return double[]
	 */
	double[] calculate(double[] xValues, double[] yValues, IMaximaDetectorSettings maximaDetectorSettings);
}
