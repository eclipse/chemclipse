/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.cml.model;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.AbstractScanIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

public class VendorIon extends AbstractScanIon implements IVendorIon {

	private static final long serialVersionUID = -4883514315841553936L;

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

		return Float.MIN_VALUE;
	}

	@Override
	public float getMaxPossibleAbundanceValue() {

		return Float.MAX_VALUE;
	}

	@Override
	public double getMinPossibleIonValue() {

		return 1.0d;
	}

	@Override
	public double getMaxPossibleIonValue() {

		return Double.MAX_VALUE;
	}
}
