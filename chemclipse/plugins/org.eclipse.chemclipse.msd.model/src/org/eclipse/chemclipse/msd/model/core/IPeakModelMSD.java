/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.IPeakModel;

/**
 * IPeakModel represents the model for the relevant IPeak values.<br/>
 * These are for example start retention time, stop retention time, background
 * abundance, peak maximum.<br/>
 * This model checks if all values are stored correctly.
 * 
 * @author eselmeister
 */
public interface IPeakModelMSD extends IPeakModel {

	/**
	 * Returns the peak mass spectrum.
	 * May return null.
	 * 
	 * @return {@link IPeakMassSpectrum}
	 */
	IPeakMassSpectrum getPeakMassSpectrum();

	/**
	 * Returns an copy of {@link IPeakMassSpectrum} from the given retention
	 * time.<br/>
	 * May return null. <br/>
	 * The retention time is given in milliseconds.<br/>
	 * If there is no mass spectrum, null will be returned.<br/>
	 * The floor value will be still returned.<br/>
	 * For example:<br/>
	 * <br/>
	 * Retention Time - Intensity 123720 - 2.640416908<br/>
	 * 124500 - 1.876085698<br/>
	 * 125280 - 13.15576144<br/>
	 * 126060 - 25.52403011<br/>
	 * 126840 - 51.88187609<br/>
	 * 127560 - 77.92704111<br/>
	 * 128340 - 100 (peakMaximum)<br/>
	 * 129120 - 92.25246091<br/>
	 * 129900 - 65.75564563<br/>
	 * 130680 - 37.84597568<br/>
	 * 131460 - 14.62651998<br/>
	 * 132240 - 5.790387956<br/>
	 * 133020 - 0<br/>
	 * <br/>
	 * getPeakAbundance(126050) would return an IMassSpectrum instance total
	 * signal is 13.15576144% of the total signal of peak maximum.
	 * 
	 * @param retentionTime
	 * @return IMassSpectrum
	 */
	IPeakMassSpectrum getPeakMassSpectrum(int retentionTime);
}
