/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.AbstractScanIon;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

public class ScanIon extends AbstractScanIon implements IScanIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -6040183420395784397L;
	// A max value for abundance
	public static final float MIN_ABUNDANCE = Float.MIN_VALUE;
	public static final float MAX_ABUNDANCE = Float.MAX_VALUE;
	// A max value for ion
	public static final double MIN_Ion = 0.5d;
	public static final double MAX_Ion = 65535.0d;

	public ScanIon(double ion) throws IonLimitExceededException {
		super(ion);
	}

	public ScanIon(double ion, boolean ignoreAbundanceLimit) throws IonLimitExceededException {
		super(ion, ignoreAbundanceLimit);
	}

	public ScanIon(double ion, float abundance) throws AbundanceLimitExceededException, IonLimitExceededException {
		super(ion, abundance);
	}

	@Override
	public float getMinPossibleAbundanceValue() {

		return MIN_ABUNDANCE;
	}

	@Override
	public float getMaxPossibleAbundanceValue() {

		return MAX_ABUNDANCE;
	}

	@Override
	public double getMinPossibleIonValue() {

		return MIN_Ion;
	}

	@Override
	public double getMaxPossibleIonValue() {

		return MAX_Ion;
	}
}
