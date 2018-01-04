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
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;

public class Peak extends AbstractPeak implements IPeak {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 8407887114553864483L;
	//
	private IPeakModel peakModel;

	/**
	 * Better use the other constructors otherwise
	 * peak model is null.
	 */
	public Peak() {
	}

	public Peak(IPeakModel peakModel) throws IllegalArgumentException {
		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public Peak(IPeakModel peakModel, String modelDescription) throws IllegalArgumentException {
		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModel getPeakModel() {

		return peakModel;
	}
}
