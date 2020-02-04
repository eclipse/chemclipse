/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.msd.model.core.AbstractChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

public class ChromatogramPeakMSD extends AbstractChromatogramPeakMSD implements IChromatogramPeakMSD {

	public ChromatogramPeakMSD(IPeakModelMSD peakModel, IChromatogramMSD chromatogram) throws IllegalArgumentException, PeakException {
		super(peakModel, chromatogram);
	}

	public ChromatogramPeakMSD(IPeakModelMSD peakModel, IChromatogramMSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {
		super(peakModel, chromatogram, modelDescription);
	}
}
