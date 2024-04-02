/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model;

import org.eclipse.chemclipse.msd.model.core.AbstractVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class VendorScan extends AbstractVendorMassSpectrum implements IVendorMassSpectrum {

	private static final long serialVersionUID = 4479075020972518951L;
	//
	public static final int MAX_IONS = 500000;
	public static final int MIN_RETENTION_TIME = 0;
	public static final int MAX_RETENTION_TIME = Integer.MAX_VALUE;

	@Override
	public int getMaxPossibleIons() {

		return MAX_IONS;
	}

	@Override
	public int getMaxPossibleRetentionTime() {

		return MAX_RETENTION_TIME;
	}

	@Override
	public int getMinPossibleRetentionTime() {

		return MIN_RETENTION_TIME;
	}

	@Override
	public IVendorMassSpectrum makeDeepCopy() throws CloneNotSupportedException {

		IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)super.clone();
		IIon clonedIon;
		//
		for(IIon ion : getIons()) {
			clonedIon = new Ion(ion.getIon(), ion.getAbundance());
			massSpectrum.addIon(clonedIon);
		}
		return massSpectrum;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
}