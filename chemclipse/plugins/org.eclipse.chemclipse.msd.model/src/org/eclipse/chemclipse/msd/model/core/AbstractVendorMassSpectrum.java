/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

public abstract class AbstractVendorMassSpectrum extends AbstractRegularMassSpectrum implements IVendorMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 5013842421250687340L;

	@Override
	public void addIon(IIon ion) {

		if(getNumberOfIons() < getMaxPossibleIons()) {
			super.addIon(ion);
		}
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		if(retentionTime >= getMinPossibleRetentionTime() && retentionTime <= getMaxPossibleRetentionTime()) {
			super.setRetentionTime(retentionTime);
		}
	}
}
