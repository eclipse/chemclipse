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
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public class ComparisonResult extends AbstractComparisonResult implements IComparisonResult {

	private static final float NULL_FACTOR_VALUE = 0.0f;

	/**
	 * Constructs the result.
	 * 
	 * @param matchQuality
	 * @param reverseMatchQuality
	 */
	public ComparisonResult(float matchFactor, float reverseMatchFactor) {
		super(matchFactor, reverseMatchFactor);
	}

	public ComparisonResult(float matchFactor, float reverseMatchFactor, float probability) {
		super(matchFactor, reverseMatchFactor, probability);
	}

	public static ComparisonResult createNullComparisonResult() {

		return new ComparisonResult(NULL_FACTOR_VALUE, NULL_FACTOR_VALUE);
	}
}
