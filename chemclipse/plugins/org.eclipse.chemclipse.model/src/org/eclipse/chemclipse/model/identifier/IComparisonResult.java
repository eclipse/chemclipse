/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.io.Serializable;

public interface IComparisonResult extends Serializable {

	float MAX_MATCH_FACTOR = 100.0f;
	float MAX_REVERSE_MATCH_FACTOR = 100.0f;
	//
	float DEF_MAX_PENALTY = 20.0f;
	float MIN_ALLOWED_PENALTY = 0.0f;
	float MAX_ALLOWED_PENALTY = 100.0f;
	//
	float MIN_ALLOWED_PROBABILITY = 0.0f;
	float MAX_ALLOWED_PROBABILITY = 100.0f;
	//
	float RATING_LIMIT_UP = 90.0f;
	float RATING_LIMIT_EQUAL = 70.0f;
	float RATING_LIMIT_DOWN = 0.0f;
	//
	String ADVISE_INCOMPLETE = "The target is maybe incomplete or recorded under bad conditions.";
	String ADVISE_IMPURITIES = "The target is maybe convoluted by impurities.";
	/*
	 * The borders should be determined to give an advise if necessary.<br/> If
	 * the fit value is high and the rfit value is low, the unknown mass
	 * spectrum is maybe convoluted by impurities.
	 */
	float MIN_LIMIT_MATCH_FACTOR = 20.0f;
	float MAX_LIMIT_MATCH_FACTOR = 80.0f;
	float MIN_LIMIT_REVERSE_MATCH_FACTOR = 20.0f;
	float MAX_LIMIT_REVERSE_MATCH_FACTOR = 80.0f;

	/**
	 * Is the result marked as a match.
	 * 
	 * @return boolean
	 */
	boolean isMatch();

	/**
	 * Set the result to be marked as a match.
	 * 
	 * @param match
	 */
	void setMatch(boolean match);

	/**
	 * Returns the match factor.
	 * 0 means no match, 100 means perfect match.
	 * 
	 * @return float
	 */
	float getMatchFactor();

	float getMatchFactorNotAdjusted();

	/**
	 * Returns the match factor direct.
	 * 0 means no match, 100 means perfect match.
	 * 
	 * @return float
	 */
	float getMatchFactorDirect();

	float getMatchFactorDirectNotAdjusted();

	/**
	 * Adjust the match factor.
	 * The penalty must be between 0 and 100:
	 * MIN_ALLOWED_PENALTY and MAX_ALLOWED_PENALTY
	 * 
	 * @param penalty
	 */
	@Deprecated
	void adjustMatchFactor(float penalty);

	/**
	 * Adds given penalty to this {@code IComparisonResult}'s penalty.
	 * 
	 * @param penalty
	 *            penalty value to add
	 */
	void setPenalty(float penalty);

	/**
	 * Resets the penalty to {@code 0}.
	 */
	void clearPenalty();

	float getPenalty();

	/**
	 * Returns the reverse match factor.
	 * 0 means no match, 100 means perfect match.
	 * 
	 * @return float
	 */
	float getReverseMatchFactor();

	/**
	 * Returns the reverse match factor direct.
	 * 0 means no match, 100 means perfect match.
	 * 
	 * @return float
	 */
	float getReverseMatchFactorDirect();

	/**
	 * Adjust the reverse match factor.
	 * The penalty must be between 0 and 100:
	 * MIN_ALLOWED_PENALTY and MAX_ALLOWED_PENALTY
	 * 
	 * @param penalty
	 */
	@Deprecated
	void adjustReverseMatchFactor(float penalty);

	/**
	 * Returns the probability.
	 * 
	 * @return float
	 */
	float getProbability();

	/**
	 * Returns an advise.<br/>
	 * For example: "the unknown peak is maybe incomplete" or
	 * "the unknown peak has maybe impurities".
	 * 
	 * @return String
	 */
	String getAdvise();

	/**
	 * Returns the rating given by the probability, match and reverse match factors.
	 * 
	 * @return float
	 */
	float getRating();
}
