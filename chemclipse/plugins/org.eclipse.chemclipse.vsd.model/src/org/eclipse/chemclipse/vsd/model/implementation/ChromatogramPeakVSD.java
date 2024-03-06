/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.implementation;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.vsd.model.core.AbstractChromatogramPeakVSD;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramPeakVSD;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IPeakModelVSD;

public class ChromatogramPeakVSD extends AbstractChromatogramPeakVSD implements IChromatogramPeakVSD {

	public ChromatogramPeakVSD(IPeakModelVSD peakModel, IChromatogramVSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {

		super(peakModel, chromatogram, modelDescription);
	}

	public ChromatogramPeakVSD(IPeakModelVSD peakModel, IChromatogramVSD chromatogram) throws IllegalArgumentException, PeakException {

		super(peakModel, chromatogram);
	}
}