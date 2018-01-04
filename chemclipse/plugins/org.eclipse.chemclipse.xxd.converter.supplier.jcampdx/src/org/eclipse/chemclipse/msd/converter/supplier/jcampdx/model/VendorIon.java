/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.AbstractScanIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

public class VendorIon extends AbstractScanIon implements IVendorIon {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -5630600980417389698L;
	// A max value for abundance
	public static final float MIN_ABUNDANCE = Float.MIN_VALUE;
	public static final float MAX_ABUNDANCE = Float.MAX_VALUE;
	// A max value for ion of
	public static final double MIN_ION = 1.0d;
	public static final double MAX_ION = 20000.0d;

	public VendorIon(double ion, boolean ignoreAbundanceLimit) throws IonLimitExceededException {
		super(ion, ignoreAbundanceLimit);
	}

	public VendorIon(double ion, float abundance) throws AbundanceLimitExceededException, IonLimitExceededException {
		super(ion, abundance);
	}

	public VendorIon(double ion) throws IonLimitExceededException {
		super(ion);
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

		return MIN_ION;
	}

	@Override
	public double getMaxPossibleIonValue() {

		return MAX_ION;
	}
}
