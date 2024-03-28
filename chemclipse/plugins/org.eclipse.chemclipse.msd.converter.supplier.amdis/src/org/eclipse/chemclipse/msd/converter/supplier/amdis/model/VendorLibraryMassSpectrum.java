/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.model;

import org.eclipse.chemclipse.msd.model.core.AbstractRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class VendorLibraryMassSpectrum extends AbstractRegularLibraryMassSpectrum implements IVendorLibraryMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 788113431263082687L;
	private String source = "";

	// -------------------------------------------IAmdisMassSpectrum
	@Override
	public String getSource() {

		return source;
	}

	@Override
	public void setSource(String source) {

		if(source != null) {
			this.source = source;
		}
	}

	// -------------------------------------------IAmdisMassSpectrum
	// -------------------------------IMassSpectrumCloneable
	/**
	 * Keep in mind, it is a covariant return.<br/>
	 * IMassSpectrum is needed. IAmdisMassSpectrum is a subtype of
	 * ILibraryMassSpectrum is a subtype of IMassSpectrum.
	 */
	@Override
	public IVendorLibraryMassSpectrum makeDeepCopy() throws CloneNotSupportedException {

		IVendorLibraryMassSpectrum massSpectrum = (IVendorLibraryMassSpectrum)super.clone();
		IIon amdisIon;
		/*
		 * The instance variables have been copied by super.clone();.<br/> The
		 * ions in the ion list need not to be removed via
		 * removeAllIons as the method super.clone() has created a new
		 * list.<br/> It is necessary to fill the list again, as the abstract
		 * super class does not know each available type of ion.<br/>
		 * Make a deep copy of all ions.
		 */
		for(IIon ion : getIons()) {
			amdisIon = new Ion(ion.getIon(), ion.getAbundance());
			massSpectrum.addIon(amdisIon);
		}
		return massSpectrum;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
	// -------------------------------IMassSpectrumCloneable
}
