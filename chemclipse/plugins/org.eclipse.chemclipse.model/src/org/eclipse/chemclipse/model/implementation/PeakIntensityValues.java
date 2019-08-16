/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;

public class PeakIntensityValues extends AbstractPeakIntensityValues implements IPeakIntensityValues {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 8596503209176077779L;

	public PeakIntensityValues() {
		super();
	}

	public PeakIntensityValues(float maxIntensity) {
		super(maxIntensity);
	}
}
