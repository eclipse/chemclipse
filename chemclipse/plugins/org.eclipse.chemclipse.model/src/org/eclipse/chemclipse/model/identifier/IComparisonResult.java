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

	String ADVISE_INCOMPLETE = "The target is maybe incomplete or recorded under bad conditions.";
	String ADVISE_IMPURITIES = "The target is maybe convoluted by impurities.";

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
