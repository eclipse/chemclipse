/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public abstract class AbstractPeakComparisonResult extends AbstractComparisonResult implements IPeakComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -4383422303109508458L;

	/**
	 * Constructs the result.
	 * 
	 * @param matchQuality
	 * @param reverseMatchQuality
	 */
	public AbstractPeakComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect);
	}

	public AbstractPeakComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
	}
}
