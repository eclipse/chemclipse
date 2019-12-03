/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.function.DoubleSupplier;

public class LazyComparisonResult extends AbstractComparisonResult {

	private static final long serialVersionUID = 1L;
	private DoubleSupplier matchFactorSupplier;
	private DoubleSupplier reverseMatchFactorSupplier;
	private DoubleSupplier matchFactorDirectSupplier;
	private DoubleSupplier reverseMatchFactorDirectSupplier;

	public LazyComparisonResult(DoubleSupplier matchFactor, DoubleSupplier reverseMatchFactor, DoubleSupplier matchFactorDirect, DoubleSupplier reverseMatchFactorDirect) {
		super(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
		matchFactorSupplier = matchFactor;
		reverseMatchFactorSupplier = reverseMatchFactor;
		matchFactorDirectSupplier = matchFactorDirect;
		reverseMatchFactorDirectSupplier = reverseMatchFactorDirect;
	}

	@Override
	public float getMatchFactorDirectNotAdjusted() {

		float mfd = super.getMatchFactorDirectNotAdjusted();
		if(Float.isNaN(mfd)) {
			setMatchFactorDirect(mfd = (float)matchFactorDirectSupplier.getAsDouble());
		}
		return mfd;
	}

	@Override
	public float getMatchFactorNotAdjusted() {

		float mf = super.getMatchFactorNotAdjusted();
		if(Float.isNaN(mf)) {
			setMatchFactor(mf = (float)matchFactorSupplier.getAsDouble());
		}
		return mf;
	}

	@Override
	public float getReverseMatchFactorDirectNotAdjusted() {

		float rmfd = super.getReverseMatchFactorDirectNotAdjusted();
		if(Float.isNaN(rmfd)) {
			setReverseMatchFactorDirect(rmfd = (float)reverseMatchFactorDirectSupplier.getAsDouble());
		}
		return rmfd;
	}

	@Override
	public float getReverseMatchFactorNotAdjusted() {

		float rmf = super.getReverseMatchFactorNotAdjusted();
		if(Float.isNaN(rmf)) {
			setReverseMatchFactor(rmf = (float)reverseMatchFactorSupplier.getAsDouble());
		}
		return rmf;
	}
}
