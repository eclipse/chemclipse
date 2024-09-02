/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public interface IMassSpectrumPeak extends ISpectrumPeak {

	/**
	 * 
	 * @return m/z
	 */
	double getIon();

	void setIon(double mz);

	/**
	 * 
	 * @return intensity
	 */
	double getAbundance();

	void setAbundance(double intensity);

	/**
	 * 
	 * 
	 * @return s/n
	 */
	double getSignalToNoise();

	void setSignalToNoise(double sn);
}
