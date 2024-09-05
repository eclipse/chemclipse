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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.model;

import org.eclipse.chemclipse.msd.model.core.AbstractScanMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.Polarity;

public class VendorScan extends AbstractScanMSD implements IVendorScan {

	private static final long serialVersionUID = -6772709008581956403L;
	//
	private Polarity polarity;

	@Override
	public Polarity getPolarity() {

		if(polarity != null) {
			return polarity;
		}
		return Polarity.NONE;
	}

	@Override
	public void setPolarity(Polarity polarity) {

		this.polarity = polarity;
	}

	@Override
	public IVendorScan makeDeepCopy() throws CloneNotSupportedException {

		IVendorScan massSpectrum = (IVendorScan)super.clone();
		IVendorIon mzXMLIon;
		/*
		 * The instance variables have been copied by super.clone();.<br/> The
		 * ions in the ion list need not to be removed via
		 * removeAllIons as the method super.clone() has created a new
		 * list.<br/> It is necessary to fill the list again, as the abstract
		 * super class does not know each available type of ion.<br/>
		 * Make a deep copy of all ions.
		 */
		for(IIon ion : getIons()) {
			mzXMLIon = new VendorIon(ion.getIon(), ion.getAbundance());
			massSpectrum.addIon(mzXMLIon);
		}
		return massSpectrum;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
}
