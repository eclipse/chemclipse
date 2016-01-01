/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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

	public PeakIntensityValues() {
		super();
	}

	public PeakIntensityValues(float maxIntensity) {
		super(maxIntensity);
	}
}
