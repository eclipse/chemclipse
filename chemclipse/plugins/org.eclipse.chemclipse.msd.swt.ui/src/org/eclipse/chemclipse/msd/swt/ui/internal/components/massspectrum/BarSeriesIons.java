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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.numeric.miscellaneous.SortOrder;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class BarSeriesIons implements IBarSeriesIons {

	private List<IBarSeriesIon> barSeriesIons;
	private BarSeriesIonComparator barSeriesIonComparator;

	public BarSeriesIons() {

		barSeriesIons = new ArrayList<IBarSeriesIon>();
		barSeriesIonComparator = new BarSeriesIonComparator(SortOrder.DESCENDING);
	}

	@Override
	public void add(IBarSeriesIon barSeriesIon) {

		barSeriesIons.add(barSeriesIon);
	}

	@Override
	public void clear() {

		barSeriesIons.clear();
	}

	@Override
	public List<IBarSeriesIon> getIonsWithHighestAbundance(int amount) {

		Collections.sort(barSeriesIons, barSeriesIonComparator);
		List<IBarSeriesIon> barSeries = new ArrayList<IBarSeriesIon>();
		if(amount > barSeriesIons.size()) {
			amount = barSeriesIons.size();
		}
		for(int index = 0; index < amount; index++) {
			addIon(barSeries, index);
		}
		return barSeries;
	}

	@Override
	public List<IBarSeriesIon> getIonsByModulo(int amount) {

		List<IBarSeriesIon> barSeries = new ArrayList<IBarSeriesIon>();
		if(amount > 0) {
			Collections.sort(barSeriesIons, barSeriesIonComparator);
			int modulo = barSeriesIons.size() / amount;
			/*
			 * The list is sorted.
			 */
			for(int index = 0; index < barSeriesIons.size(); index++) {
				if(barSeries.size() <= amount) {
					addIon(barSeries, index);
				} else {
					if(index % modulo == 0) {
						addIon(barSeries, index);
					}
				}
			}
		}
		return barSeries;
	}

	@Override
	public IBarSeriesIon getBarSeriesIon(int index) {

		if(index >= 0 && index < size()) {
			return barSeriesIons.get(index);
		}
		return null;
	}

	@Override
	public int size() {

		return barSeriesIons.size();
	}

	private void addIon(List<IBarSeriesIon> barSeries, int index) {

		/*
		 * The abundance of the ion must be > 0.
		 */
		IBarSeriesIon barSeriesIon = barSeriesIons.get(index);
		if(barSeriesIon.getAbundance() > 0.0d) {
			barSeries.add(barSeriesIon);
		}
	}
}
