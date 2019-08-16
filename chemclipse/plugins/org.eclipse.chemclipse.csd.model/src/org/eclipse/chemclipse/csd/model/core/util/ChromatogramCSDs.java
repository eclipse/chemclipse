/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.util;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

/**
 * Utility class for {@link IChromatogramCSD} related stuff.
 * 
 * @author Alexander Kerner
 *
 */
public class ChromatogramCSDs {

	/**
	 * Convenience method to add a {@link IPeakCSD} to an {@link IChromatogramCSD}.
	 * </p>
	 * Internally a {@link IChromatogramPeakCSD} is created, added to given chromatogram and finally returned.
	 * <br>
	 * {@link IIntegrationEntry integration entries} and {@link IIdentificationTarget peak targets} are copied from given peak.
	 * </p>
	 * 
	 * @param chromatogram
	 *            {@link IChromatogramCSD} to which given peak should be added
	 * @param peak
	 *            {@link IPeakCSD} peak to add to given chromatogram
	 * 
	 * @return {@link IChromatogramPeakCSD} which was created and added to given {@link IChromatogramCSD}
	 * 
	 * @see IPeakCSD
	 * @see IChromatogramCSD
	 * @see IChromatogramPeakCSD
	 * @see IIdentificationTarget
	 * @see IIntegrationEntry
	 */
	public static ChromatogramPeakCSD addPeakToChromatogram(IChromatogramCSD chromatogram, IPeakCSD peak) {

		// TODO: find common super type implementation (MSD, CSD, WSD). -> Generic ChromatogramPeak type is needed
		ChromatogramPeakCSD chromatogramPeak = new ChromatogramPeakCSD(peak.getPeakModel(), chromatogram);
		chromatogramPeak.addAllIntegrationEntries(peak.getIntegrationEntries());
		chromatogramPeak.getTargets().addAll(peak.getTargets());
		chromatogram.addPeak(chromatogramPeak);
		return chromatogramPeak;
	}
}
