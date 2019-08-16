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
package org.eclipse.chemclipse.wsd.model.core;

import org.eclipse.chemclipse.model.core.AbstractPeak;

public abstract class AbstractPeakWSD extends AbstractPeak implements IPeakWSD {

	private IPeakModelWSD peakModel;

	public AbstractPeakWSD(IPeakModelWSD peakModel) throws IllegalArgumentException {
		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public AbstractPeakWSD(IPeakModelWSD peakModel, String modelDescription) throws IllegalArgumentException {
		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModelWSD getPeakModel() {

		return peakModel;
	}
}
