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
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.IPeak;

public interface IPeakMSD extends IPeak {

	/**
	 * Returns the actual peak model instance.<br/>
	 * The peak model describes the peak width, start and stop retention time
	 * and so on.<br/>
	 * Why are IPeak and IPeakModel are differentiated? Because you can apply an
	 * IPeakModel instance without having knowledge of the parent chromatogram.<br/>
	 * But this leaves also to the situation that you can't calculate the signal
	 * to noise ratio or the peak abundance at a given scan number.<br/>
	 * These values are only accessible when a valid parent chromatogram
	 * instance exists.
	 * 
	 * @return IPeakModel
	 */
	IPeakModelMSD getPeakModel();

	/**
	 * Returns the extracted peak mass spectrum. See also
	 * getChromatogramMassSpectrum().
	 * 
	 * @return IPeakMassSpectrum
	 */
	IPeakMassSpectrum getExtractedMassSpectrum();
}
