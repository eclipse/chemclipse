/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public interface IComparisonResult {

	float MAX_MATCH_FACTOR = 100.0f;
	float MAX_REVERSE_MATCH_FACTOR = 100.0f;
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
	 * Returns the match factor.
	 * 0 means no match, 100 means perfect match.
	 * 
	 * @return float
	 */
	float getMatchFactor();

	/**
	 * Returns the reverse match factor.
	 * 0 means no match, 100 means perfect match.
	 * 
	 * @return float
	 */
	float getReverseMatchFactor();

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
}
