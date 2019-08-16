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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.io;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;

public class ChromatogramSupport {

	private double parentIon;
	private double collisionEnergy;
	private double daughterIon;
	private Map<Integer, Float> retentionTimeIntensities;

	public ChromatogramSupport(Number[] numbersRetentionTimeInMinutes, Number[] numbersIntensity) {
		retentionTimeIntensities = new HashMap<Integer, Float>();
		int size = numbersRetentionTimeInMinutes.length;
		for(int i = 0; i < size; i++) {
			int retentionTime = (int)(numbersRetentionTimeInMinutes[i].doubleValue() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			float intensity = numbersIntensity[i].floatValue();
			retentionTimeIntensities.put(retentionTime, intensity);
		}
	}

	public double getParentIon() {

		return parentIon;
	}

	public void setParentIon(double parentIon) {

		this.parentIon = parentIon;
	}

	public double getCollisionEnergy() {

		return collisionEnergy;
	}

	public void setCollisionEnergy(double collisionEnergy) {

		this.collisionEnergy = collisionEnergy;
	}

	public double getDaughterIon() {

		return daughterIon;
	}

	public void setDaughterIon(double daughterIon) {

		this.daughterIon = daughterIon;
	}

	public Map<Integer, Float> getRetentionTimeIntensities() {

		return retentionTimeIntensities;
	}
}
