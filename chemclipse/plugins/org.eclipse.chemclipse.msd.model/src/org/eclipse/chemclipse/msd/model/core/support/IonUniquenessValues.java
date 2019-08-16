/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eselmeister
 */
public class IonUniquenessValues implements IIonUniquenessValues {

	private static Map<Integer, Float> probabilityValues;

	public IonUniquenessValues() {
		probabilityValues = new HashMap<Integer, Float>();
	}

	@Override
	public void add(int ion, float probabilityValue) {

		if(probabilityValue >= MIN_PROBABILITY_VALUE && probabilityValue <= MAX_PROBABILITY_VALUE) {
			probabilityValues.put(ion, probabilityValue);
		}
	}

	@Override
	public void remove(int ion) {

		probabilityValues.remove(ion);
	}

	@Override
	public float getUniquenessValue(int ion) {

		float result = getPropabilityValue(ion);
		return 1.0f - result;
	}

	@Override
	public float getPropabilityValue(int ion) {

		Float result = probabilityValues.get(ion);
		if(result == null) {
			return 0.0f;
		} else {
			return result;
		}
	}
}
