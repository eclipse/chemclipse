/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.chemclipse.msd.model.core.IIon;

public class IonCombinedComparator implements Comparator<IIon>, Serializable {

	private static final long serialVersionUID = -109573471700765379L;
	private final Comparator<IIon> firstComparator;
	private final Comparator<IIon> secondComparator;

	public IonCombinedComparator(IonComparatorMode desiredMode) {

		switch(desiredMode) {
			case ABUNDANCE_FIRST:
				firstComparator = new IonAbundanceComparator();
				secondComparator = new IonValueComparator();
				break;
			case MZ_FIRST:
				firstComparator = new IonValueComparator();
				secondComparator = new IonAbundanceComparator();
				break;
			default:
				firstComparator = new IonAbundanceComparator();
				secondComparator = new IonValueComparator();
				break;
		}
	}

	@Override
	public int compare(IIon arg0, IIon arg1) {

		int result = firstComparator.compare(arg0, arg1);
		if(result != 0) {
			return result;
		}
		result = secondComparator.compare(arg0, arg1);
		return result;
	}
}
