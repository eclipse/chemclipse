/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import org.eclipse.chemclipse.model.core.AbstractPeakModel;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.PeakException;

public abstract class AbstractPeakModelCSD extends AbstractPeakModel implements IPeakModelCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 1580595192753292038L;

	public AbstractPeakModelCSD(IScan peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance) throws IllegalArgumentException, PeakException {
		super(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
	}
}
