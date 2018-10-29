/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.wsd.model.core.AbstractPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakModelWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;

public class PeakWSD extends AbstractPeakWSD implements IPeakWSD {

	public PeakWSD(IPeakModelWSD peakModel, String modelDescription) throws IllegalArgumentException {
		super(peakModel, modelDescription);
	}

	public PeakWSD(IPeakModelWSD peakModel) throws IllegalArgumentException {
		super(peakModel);
	}
}
