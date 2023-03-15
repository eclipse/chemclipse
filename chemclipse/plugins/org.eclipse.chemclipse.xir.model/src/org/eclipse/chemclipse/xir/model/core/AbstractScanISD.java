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

import org.eclipse.chemclipse.model.core.AbstractScan;

public abstract class AbstractScanISD extends AbstractScan implements IScanISD {

	private static final long serialVersionUID = 4376839034057299437L;

	@Override
	public void adjustTotalSignal(float totalSignal) {

		if(totalSignal < 0.0f || Float.isNaN(totalSignal) || Float.isInfinite(totalSignal)) {
			return;
		}
		/*
		 * Avoid a division by zero exception :-).
		 */
		if(getTotalSignal() == 0.0f) {
			return;
		}
		//
		double base = 100.0d;
		double correctionFactor = ((base / getTotalSignal()) * totalSignal) / base;
		/*
		 * TODO - also adjust absorbance, transmittance?
		 */
		for(ISignalXIR scanSignal : getProcessedSignals()) {
			double scattering = scanSignal.getScattering();
			scattering *= correctionFactor;
			scanSignal.setScattering(scattering);
		}
	}
}