/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
	//
	private DoubleSupplier matchFactorSupplier;
	private DoubleSupplier reverseMatchFactorSupplier;
	private DoubleSupplier matchFactorDirectSupplier;
	private DoubleSupplier reverseMatchFactorDirectSupplier;
	/*
	 * Order of match calculation:
	 * 1) Match Factor
	 * 2) Reverse Match Factor
	 * 3) Match Factor Direct
	 * 4) Reverse Match Factor Direct
	 */
	private MatchConstraints matchConstraints;

	public LazyComparisonResult(DoubleSupplier matchFactor, DoubleSupplier reverseMatchFactor, DoubleSupplier matchFactorDirect, DoubleSupplier reverseMatchFactorDirect, MatchConstraints matchConstraints) {
		super(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
		matchFactorSupplier = matchFactor;
		reverseMatchFactorSupplier = reverseMatchFactor;
		matchFactorDirectSupplier = matchFactorDirect;
		reverseMatchFactorDirectSupplier = reverseMatchFactorDirect;
		this.matchConstraints = matchConstraints;
	}

	@Override
	public float getMatchFactorNotAdjusted() {

		float matchFactor = super.getMatchFactorNotAdjusted();
		if(Float.isNaN(matchFactor)) {
			setMatchFactor(matchFactor = (float)matchFactorSupplier.getAsDouble());
		}
		return matchFactor;
	}

	@Override
	public float getReverseMatchFactorNotAdjusted() {

		float reverseMatchFactor = super.getReverseMatchFactorNotAdjusted();
		if(Float.isNaN(reverseMatchFactor)) {
			if(runReverseMatch()) {
				setReverseMatchFactor(reverseMatchFactor = (float)reverseMatchFactorSupplier.getAsDouble());
			} else {
				setReverseMatchFactor(0.0f);
			}
		}
		return reverseMatchFactor;
	}

	@Override
	public float getMatchFactorDirectNotAdjusted() {

		float matchFactorDirect = super.getMatchFactorDirectNotAdjusted();
		if(Float.isNaN(matchFactorDirect)) {
			if(runMatchDirect()) {
				setMatchFactorDirect(matchFactorDirect = (float)matchFactorDirectSupplier.getAsDouble());
			} else {
				setMatchFactorDirect(0.0f);
			}
		}
		return matchFactorDirect;
	}

	@Override
	public float getReverseMatchFactorDirectNotAdjusted() {

		float reverseMatchFactorDirect = super.getReverseMatchFactorDirectNotAdjusted();
		if(Float.isNaN(reverseMatchFactorDirect)) {
			if(runReverseMatchDirect()) {
				setReverseMatchFactorDirect(reverseMatchFactorDirect = (float)reverseMatchFactorDirectSupplier.getAsDouble());
			} else {
				setReverseMatchFactorDirect(0.0f);
			}
		}
		return reverseMatchFactorDirect;
	}

	private boolean runReverseMatch() {

		return getMatchFactorNotAdjusted() >= matchConstraints.getMinMatchFactor();
	}

	private boolean runMatchDirect() {

		if(runReverseMatch()) {
			return getReverseMatchFactorNotAdjusted() >= matchConstraints.getMinReverseMatchFactor();
		}
		return false;
	}

	private boolean runReverseMatchDirect() {

		if(runMatchDirect()) {
			return getMatchFactorDirectNotAdjusted() >= matchConstraints.getMinMatchFactorDirect();
		}
		return false;
	}
}
