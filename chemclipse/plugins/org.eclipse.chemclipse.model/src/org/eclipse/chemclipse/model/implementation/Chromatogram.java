/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

@SuppressWarnings({"serial", "rawtypes"})
public class Chromatogram extends AbstractChromatogram implements IChromatogram {

	@Override
	public void recalculateTheNoiseFactor() {

	}

	@Override
	public float getSignalToNoiseRatio(float abundance) {

		return 0;
	}

	@Override
	public double getPeakIntegratedArea() {

		return 0;
	}

	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

	}
}
