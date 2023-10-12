/*******************************************************************************
 * Copyright (c) 2010, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - getPenalty and Matchfactors should be accessed by getters and not direct field access
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public abstract class AbstractComparisonResult implements IComparisonResult {

	private static final long serialVersionUID = 1295884624032029498L;
	//
	private boolean isMatch = false;
	private float matchFactor = 0.0f;
	private float matchFactorDirect = 0.0f;
	private float reverseMatchFactor = 0.0f;
	private float reverseMatchFactorDirect = 0.0f;
	private float probability = 0.0f;
	private float inLibFactor = 0.0f;
	private float penalty = 0.0f;
	/*
	 * It's not planned to allow setting a specific
	 * rating due to the reason, that the values of
	 * the target/comparison result are saved e.g.
	 * in the Open Chromatography Binary (*.ocb), but
	 * the rating supplier with its algorithm is not.
	 * Hence, when loading a file again, the default
	 * rating supplier calculates the score and might
	 * lead to an inconsistent status.
	 */
	private IRatingSupplier ratingSupplier = new RatingSupplier(this);

	public AbstractComparisonResult(float matchFactor) {

		this(matchFactor, matchFactor, matchFactor, matchFactor);
	}

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {

		this(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, MAX_ALLOWED_PROBABILITY);
	}

	public AbstractComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {

		this.matchFactor = matchFactor;
		this.reverseMatchFactor = reverseMatchFactor;
		this.matchFactorDirect = matchFactorDirect;
		this.reverseMatchFactorDirect = reverseMatchFactorDirect;
		setProbability(probability);
	}

	public AbstractComparisonResult(IComparisonResult comparisonResult) {

		this(comparisonResult.getMatchFactor(), comparisonResult.getReverseMatchFactor(), comparisonResult.getMatchFactorDirect(), comparisonResult.getReverseMatchFactorDirect());
	}

	@Override
	public float getPenalty() {

		return penalty;
	}

	@Override
	public void clearPenalty() {

		setPenalty(0);
	}

	@Override
	public void setPenalty(float penalty) {

		if(penalty >= MIN_ALLOWED_PENALTY && penalty <= MAX_ALLOWED_PENALTY) {
			this.penalty = penalty;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void addPenalty(float penalty) {

		float newPenalty = getPenalty() + penalty;
		if(newPenalty > MAX_ALLOWED_PENALTY) {
			setPenalty(MAX_ALLOWED_PENALTY);
		} else if(newPenalty < MIN_ALLOWED_PENALTY) {
			setPenalty(MIN_ALLOWED_PENALTY);
		} else {
			setPenalty(newPenalty);
		}
	}

	@Override
	public boolean isMatch() {

		return isMatch;
	}

	@Override
	public AbstractComparisonResult setMatch(boolean match) {

		this.isMatch = match;
		return this;
	}

	@Override
	public final float getMatchFactor() {

		return getAdjustedValue(getMatchFactorNotAdjusted(), getPenalty());
	}

	@Override
	public final float getMatchFactorDirect() {

		return getAdjustedValue(getMatchFactorDirectNotAdjusted(), getPenalty());
	}

	@Override
	public float getMatchFactorNotAdjusted() {

		return matchFactor;
	}

	@Override
	public float getMatchFactorDirectNotAdjusted() {

		return matchFactorDirect;
	}

	@Override
	public final float getReverseMatchFactor() {

		return getAdjustedValue(getReverseMatchFactorNotAdjusted(), getPenalty());
	}

	@Override
	public final float getReverseMatchFactorDirect() {

		return getAdjustedValue(getReverseMatchFactorDirectNotAdjusted(), getPenalty());
	}

	@Override
	public float getReverseMatchFactorNotAdjusted() {

		return reverseMatchFactor;
	}

	@Override
	public float getReverseMatchFactorDirectNotAdjusted() {

		return reverseMatchFactorDirect;
	}

	@Override
	public float getProbability() {

		return probability;
	}

	@Override
	public float getInLibFactor() {

		return inLibFactor;
	}

	@Override
	public void setInLibFactor(float inLibFactor) {

		this.inLibFactor = inLibFactor;
	}

	@Override
	public IRatingSupplier getRatingSupplier() {

		return ratingSupplier;
	}

	public static float getAdjustedValue(float value, float penalty) {

		float result = value - penalty;
		if(result < 0) {
			return 0;
		}
		//
		return result;
	}

	@Override
	public int compareTo(IComparisonResult comparisonResult) {

		int result = Boolean.compare(this.isMatch(), comparisonResult.isMatch());
		if(result == 0) {
			result = Float.compare(this.getMatchFactor(), comparisonResult.getMatchFactor());
		}
		if(result == 0) {
			result = Float.compare(this.getReverseMatchFactor(), comparisonResult.getReverseMatchFactor());
		}
		if(result == 0) {
			result = Float.compare(this.getMatchFactorDirect(), comparisonResult.getMatchFactorDirect());
		}
		if(result == 0) {
			result = Float.compare(this.getReverseMatchFactorDirect(), comparisonResult.getReverseMatchFactorDirect());
		}
		return result;
	}

	protected void setMatchFactor(float matchFactor) {

		this.matchFactor = matchFactor;
	}

	protected void setMatchFactorDirect(float matchFactorDirect) {

		this.matchFactorDirect = matchFactorDirect;
	}

	protected void setReverseMatchFactor(float reverseMatchFactor) {

		this.reverseMatchFactor = reverseMatchFactor;
	}

	protected void setReverseMatchFactorDirect(float reverseMatchFactorDirect) {

		this.reverseMatchFactorDirect = reverseMatchFactorDirect;
	}

	private void setProbability(float probability) {

		if(probability >= MIN_ALLOWED_PROBABILITY && probability <= MAX_ALLOWED_PROBABILITY) {
			this.probability = probability;
		} else {
			this.probability = 0.0f;
		}
	}
}