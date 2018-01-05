/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

public class WavelengthBounds implements IWavelengthBounds {

	private IScanSignalWSD lowestWavelength = null;
	private IScanSignalWSD highestWavelength = null;

	public WavelengthBounds(IScanSignalWSD lowestWavelength, IScanSignalWSD highestWavelength) {
		if(lowestWavelength != null && highestWavelength != null) {
			/*
			 * Change the order if necessary.
			 */
			if(lowestWavelength.getWavelength() > highestWavelength.getWavelength()) {
				this.highestWavelength = lowestWavelength;
				this.lowestWavelength = highestWavelength;
			} else {
				this.lowestWavelength = lowestWavelength;
				this.highestWavelength = highestWavelength;
			}
		}
	}

	@Override
	public IScanSignalWSD getLowestWavelength() {

		return lowestWavelength;
	}

	@Override
	public IScanSignalWSD getHighestWavelength() {

		return highestWavelength;
	}
}
