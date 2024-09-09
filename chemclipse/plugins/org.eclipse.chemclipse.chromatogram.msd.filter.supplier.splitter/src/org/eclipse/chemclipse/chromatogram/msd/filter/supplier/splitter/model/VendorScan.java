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

import org.eclipse.chemclipse.msd.model.core.AbstractRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class VendorScan extends AbstractRegularMassSpectrum implements IRegularMassSpectrum {

	private static final long serialVersionUID = 4479075020372518951L;

	@Override
	public IRegularMassSpectrum makeDeepCopy() throws CloneNotSupportedException {

		IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)super.clone();
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