/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.splash;

import java.util.Comparator;

import org.eclipse.chemclipse.msd.model.core.IIon;

public class MassThenIntensityComparator implements Comparator<IIon> {

	public int compare(IIon i1, IIon i2) {

		int result = Double.compare(i1.getIon(), (i2.getIon()));
		if(result == 0) {
			result = Double.compare(i2.getAbundance(), i1.getAbundance());
		}
		return result;
	}
}
