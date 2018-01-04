/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings;

/**
 * @author eselmeister
 */
public class CodaSettings implements ICodaSettings {

	private float codaThreshold = ICodaSettings.DEFAULT_CODA_THRESHOLD;

	@Override
	public float getCodaThreshold() {

		return codaThreshold;
	}

	@Override
	public void setCodaThreshold(float codaThreshold) {

		if(codaThreshold >= ICodaSettings.MIN_CODA_THRESHOLD && codaThreshold <= ICodaSettings.MAX_CODA_THRESHOLD) {
			this.codaThreshold = codaThreshold;
		}
	}
}
