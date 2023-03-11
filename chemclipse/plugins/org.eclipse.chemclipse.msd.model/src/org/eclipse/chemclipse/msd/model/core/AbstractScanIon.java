/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonIsNullException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonTransitionIsNullException;

public abstract class AbstractScanIon extends AbstractIon implements IScanIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 892026628429907833L;
	private boolean ignoreAbundanceLimit = false;

	// ----------------------Constructors
	protected AbstractScanIon(double ion) throws IonLimitExceededException {

		super(ion);
	}

	protected AbstractScanIon(double ion, boolean ignoreAbundanceLimit) throws IonLimitExceededException {

		super(ion);
		this.ignoreAbundanceLimit = ignoreAbundanceLimit;
	}

	protected AbstractScanIon(double ion, float abundance) throws AbundanceLimitExceededException, IonLimitExceededException {

		super(ion, abundance);
	}

	protected AbstractScanIon(double ion, float abundance, IIonTransition ionTransition) throws AbundanceLimitExceededException, IonLimitExceededException, IonTransitionIsNullException {

		super(ion, abundance, ionTransition);
	}

	protected AbstractScanIon(IIon ion, IIonTransition ionTransition) throws AbundanceLimitExceededException, IonLimitExceededException, IonIsNullException, IonTransitionIsNullException {

		super(ion, ionTransition);
	}

	protected AbstractScanIon(IIon ion) throws AbundanceLimitExceededException, IonLimitExceededException, IonIsNullException {

		super(ion);
	}

	// ----------------------Constructors
	@Override
	public AbstractScanIon setAbundance(float abundance) throws AbundanceLimitExceededException {

		if(ignoreAbundanceLimit) {
			super.setAbundance(abundance);
		} else if(abundance < getMinPossibleAbundanceValue() || abundance > getMaxPossibleAbundanceValue() || Float.isNaN(abundance)) {
			throw new AbundanceLimitExceededException("The value abundance: " + abundance + " is out of limit " + getMinPossibleAbundanceValue() + " - " + getMaxPossibleAbundanceValue());
		} else {
			super.setAbundance(abundance);
		}
		return this;
	}

	@Override
	public AbstractScanIon setIon(double ion) throws IonLimitExceededException {

		/*
		 * IIon.TIC_Ion is used to set a TIC value in case of
		 * IChromatogramOverview.
		 */
		if(ion == IIon.TIC_ION) {
			super.setIon(ion);
		} else if(ion < getMinPossibleIonValue() || ion > getMaxPossibleIonValue() || Double.isNaN(ion)) {
			throw new IonLimitExceededException("The value ion: " + ion + " is out of limit " + getMinPossibleIonValue() + " - " + getMaxPossibleIonValue());
		} else {
			super.setIon(ion);
		}
		return this;
	}

	@Override
	public boolean isIgnoreAbundanceLimit() {

		return ignoreAbundanceLimit;
	}

	@Override
	public void setIgnoreAbundanceLimit(boolean ignoreAbundanceLimit) {

		this.ignoreAbundanceLimit = ignoreAbundanceLimit;
	}

	// -----------------------------toString
	@Override
	public boolean equals(Object otherObject) {

		if(super.equals(otherObject)) {
			AbstractScanIon ion = (AbstractScanIon)otherObject;
			return ignoreAbundanceLimit == ion.isIgnoreAbundanceLimit();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {

		return super.hashCode() + 7 * Boolean.valueOf(ignoreAbundanceLimit).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("ignoreAbundanceLimit=" + ignoreAbundanceLimit);
		builder.append(",");
		builder.append("maxPossibleAbundanceValue=" + getMaxPossibleAbundanceValue());
		builder.append(",");
		builder.append("maxPossibleIonValue=" + getMaxPossibleIonValue());
		builder.append(",");
		builder.append("lowestInvalidAbundanceValue=" + getMinPossibleAbundanceValue());
		builder.append(",");
		builder.append("minPossibleIonValue=" + getMinPossibleIonValue());
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------toString
}
