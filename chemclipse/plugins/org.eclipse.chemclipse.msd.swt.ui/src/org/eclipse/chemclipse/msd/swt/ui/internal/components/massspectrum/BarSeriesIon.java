/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BarSeriesIon implements IBarSeriesIon {

	private double ion;
	private double abundance;
	private int index;

	public BarSeriesIon(double ion, double abundance, int index) {
		this.ion = ion;
		this.abundance = abundance;
		this.index = index;
	}

	@Override
	public double getIon() {

		return ion;
	}

	@Override
	public double getAbundance() {

		return abundance;
	}

	@Override
	public int getIndex() {

		return index;
	}
}
