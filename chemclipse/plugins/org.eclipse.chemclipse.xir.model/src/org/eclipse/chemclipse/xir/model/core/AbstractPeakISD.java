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
package org.eclipse.chemclipse.xir.model.core;

import org.eclipse.chemclipse.model.core.AbstractPeak;

public class AbstractPeakISD extends AbstractPeak implements IPeakISD {

	private IPeakModelISD peakModel;

	public AbstractPeakISD(IPeakModelISD peakModel) throws IllegalArgumentException {

		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public AbstractPeakISD(IPeakModelISD peakModel, String modelDescription) throws IllegalArgumentException {

		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModelISD getPeakModel() {

		return peakModel;
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