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
package org.eclipse.chemclipse.msd.model.core;

public abstract class AbstractPeakIon extends AbstractIon implements IPeakIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -3520745862587712334L;
	private float uncertaintyFactor = 1.0f;
	private PeakIonType peakIonType = PeakIonType.NO_TYPE;

	protected AbstractPeakIon(double ion) {

		super(ion);
	}

	protected AbstractPeakIon(double ion, float abundance) {

		super(ion, abundance);
	}

	protected AbstractPeakIon(IIon ion) {

		super(ion.getIon(), ion.getAbundance());
	}

	/**
	 * Sets the uncertainty factor of the ion.<br/>
	 * A factor of 0.0f mean 0%, a factor of 1.0f means 100%.<br/>
	 * 100% means, that this ions belongs surely to the corresponding
	 * mass spectrum.
	 * 
	 * @param ion
	 * @param abundance
	 * @param uncertaintyFactor
	 * @throws AbundanceLimitExceededException
	 * @throws IonLimitExceededException
	 */
	protected AbstractPeakIon(double ion, float abundance, float uncertaintyFactor) {

		super(ion, abundance);
		/*
		 * Set the uncertainty factor.
		 */
		setUncertaintyFactor(uncertaintyFactor);
	}

	// ------------------------------------------------IPeakIon
	@Override
	public float getUncertaintyFactor() {

		return uncertaintyFactor;
	}

	@Override
	public void setUncertaintyFactor(float uncertaintyFactor) {

		if(uncertaintyFactor >= 0.0f && uncertaintyFactor <= 1.0f) {
			this.uncertaintyFactor = uncertaintyFactor;
		}
	}

	@Override
	public PeakIonType getPeakIonType() {

		return peakIonType;
	}

	@Override
	public void setPeakIonType(PeakIonType peakIonType) {

		if(peakIonType != null) {
			this.peakIonType = peakIonType;
		}
	}
	// ------------------------------------------------IPeakIon
}
