/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.List;

public interface IQuantitationPeaksMSD {

	/**
	 * The peaks used to quantify the substance.
	 * 
	 * @return {@link IQuantitationPeakMSD}
	 */
	List<IQuantitationPeakMSD> getQuantitationPeaks();

	/**
	 * Adds a quantitation peak to the list of peaks.
	 * 
	 * @param quantitationPeakMSD
	 */
	void addQuantitationPeak(IQuantitationPeakMSD quantitationPeakMSD);

	/**
	 * Removes the quantitation peak from the list of peaks.
	 * 
	 * @param quantitationPeakMSD
	 */
	void removeQuantitationPeak(IQuantitationPeakMSD quantitationPeakMSD);
}
