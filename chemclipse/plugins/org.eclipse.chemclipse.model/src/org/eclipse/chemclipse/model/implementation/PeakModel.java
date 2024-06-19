/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractPeakModel;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.PeakException;

public class PeakModel extends AbstractPeakModel implements IPeakModel {

	private static final long serialVersionUID = -5942483159915658483L;

	public PeakModel(IScan peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance) throws IllegalArgumentException, PeakException {

		/**
		 * By default, the strict model is used to ensure backward compatibility.
		 * If the increasing/decreasing tangent can't be calculated, the less strict model is used.
		 */
		super(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance, true);
	}
}