/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity;

public interface IMassSpectrumPurityResult {

	/**
	 * The fit value describes how good the unknown fits into the reference.<br/>
	 * Zero means that there are no mass spectra which could be found in the
	 * other mass spectrum.<br/>
	 * A value of 100 says that all mass spectra of unknown can be found in
	 * reference.
	 * 
	 * @return float
	 */
	float getFitValue();

	/**
	 * The reverse fit describes the fit value just the way around.<br/>
	 * So reference is compared to unknown.
	 * 
	 * @return float
	 */
	float getReverseFitValue();
}
