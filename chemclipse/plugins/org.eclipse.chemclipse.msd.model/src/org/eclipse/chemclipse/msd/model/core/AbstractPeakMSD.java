/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove equals
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.AbstractPeak;

public abstract class AbstractPeakMSD extends AbstractPeak implements IPeakMSD {

	private final IPeakModelMSD peakModel;

	public AbstractPeakMSD(IPeakModelMSD peakModel) throws IllegalArgumentException {
		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public AbstractPeakMSD(IPeakModelMSD peakModel, String modelDescription) throws IllegalArgumentException {
		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModelMSD getPeakModel() {

		return peakModel;
	}

	@Override
	public IPeakMassSpectrum getExtractedMassSpectrum() {

		return peakModel.getPeakMassSpectrum();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("peakModel=" + peakModel);
		builder.append("]");
		return builder.toString();
	}
}
