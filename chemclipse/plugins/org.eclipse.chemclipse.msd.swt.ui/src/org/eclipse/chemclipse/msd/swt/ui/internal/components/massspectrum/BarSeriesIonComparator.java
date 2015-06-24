/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum;

import java.util.Comparator;

import org.eclipse.chemclipse.numeric.miscellaneous.SortOrder;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class BarSeriesIonComparator implements Comparator<IBarSeriesIon> {

	private SortOrder sortOrder;

	public BarSeriesIonComparator(SortOrder sortOrder) {

		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IBarSeriesIon barSeriesIon1, IBarSeriesIon barSeriesIon2) {

		double abundance1 = barSeriesIon1.getAbundance();
		double abundance2 = barSeriesIon2.getAbundance();
		int result = Double.compare(abundance1, abundance2);
		/*
		 * Switch the sort order.
		 */
		if(sortOrder == SortOrder.DESCENDING) {
			result *= -1;
		}
		return result;
	}
}
