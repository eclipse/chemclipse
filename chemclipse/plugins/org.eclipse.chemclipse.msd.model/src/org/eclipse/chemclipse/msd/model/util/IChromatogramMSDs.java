/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.util;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;

/**
 * Utility class for {@link IChromatogramMSD} related stuff.
 * 
 * @author Alexander Kerner
 *
 */
public class IChromatogramMSDs {

	/**
	 * Convenience method to add a {@link IPeakMSD} to an {@link IChromatogramMSD}.
	 * </p>
	 * Internally a {@link IChromatogramPeakMSD} is created, added to given chromatogram and finally returned.
	 * <br>
	 * {@link IIntegrationEntry integration entries} and {@link IPeakTarget peak targets} are copied from given peak.
	 * </p>
	 * 
	 * @param chromatogramMSD
	 *            {@link IChromatogramMSD} to which given peak should be added
	 * @param peakMSD
	 *            {@link IPeakMSD} peak to add to given chromatogram
	 * 
	 * @return {@link IChromatogramPeakMSD} which was created and added to given {@link IChromatogramMSD}
	 * 
	 * @see IPeakMSD
	 * @see IChromatogramMSD
	 * @see IChromatogramPeakMSD
	 * @see IPeakTarget
	 * @see IIntegrationEntry
	 */
	public static ChromatogramPeakMSD addPeakToChromatogram(IChromatogramMSD chromatogramMSD, IPeakMSD peakMSD) {

		ChromatogramPeakMSD chromatogramPeakMSD = new ChromatogramPeakMSD(peakMSD.getPeakModel(), chromatogramMSD);
		chromatogramPeakMSD.addAllIntegrationEntries(peakMSD.getIntegrationEntries());
		chromatogramPeakMSD.addAllTargets(peakMSD.getTargets());
		chromatogramMSD.addPeak(chromatogramPeakMSD);
		return chromatogramPeakMSD;
	}
}
