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
package org.eclipse.chemclipse.xir.model.implementation;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.xir.model.core.AbstractChromatogramPeakISD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramPeakISD;
import org.eclipse.chemclipse.xir.model.core.IPeakModelISD;

public class ChromatogramPeakISD extends AbstractChromatogramPeakISD implements IChromatogramPeakISD {

	public ChromatogramPeakISD(IPeakModelISD peakModel, IChromatogramISD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {

		super(peakModel, chromatogram, modelDescription);
	}

	public ChromatogramPeakISD(IPeakModelISD peakModel, IChromatogramISD chromatogram) throws IllegalArgumentException, PeakException {

		super(peakModel, chromatogram);
	}
}