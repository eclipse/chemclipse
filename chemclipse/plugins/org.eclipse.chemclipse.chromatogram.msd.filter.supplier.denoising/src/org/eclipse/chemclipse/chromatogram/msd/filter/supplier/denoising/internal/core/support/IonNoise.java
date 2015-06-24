/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

public class IonNoise implements Comparable<IonNoise> {

	private int ion;
	private float abundance;

	public IonNoise(int ion, float abundance) {

		this.ion = ion;
		this.abundance = abundance;
	}

	/**
	 * Returns the ion.
	 * 
	 * @return int
	 */
	public int getIon() {

		return ion;
	}

	/**
	 * Returns the abundance.
	 * 
	 * @return float
	 */
	public float getAbundance() {

		return abundance;
	}

	/**
	 * Do not remove this method, see: Denoising.calculateCorrelationFactor()
	 */
	@Override
	public int compareTo(IonNoise other) {

		return (int)(other.getAbundance() - abundance);
	}
}
