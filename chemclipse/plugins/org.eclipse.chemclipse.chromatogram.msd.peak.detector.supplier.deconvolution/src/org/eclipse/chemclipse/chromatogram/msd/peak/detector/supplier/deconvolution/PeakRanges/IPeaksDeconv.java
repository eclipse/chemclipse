/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges;

public interface IPeaksDeconv {

	String getWhereGotPeak();

	void setWhereGotPeak(String text);

	void addPeak(IPeakDeconv peak);

	IPeakDeconv getPeakDeconv(int value);

	int sizePeakDeconv();
}
