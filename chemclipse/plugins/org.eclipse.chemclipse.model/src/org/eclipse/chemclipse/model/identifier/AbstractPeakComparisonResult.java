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

public abstract class AbstractPeakComparisonResult extends AbstractComparisonResult implements IPeakComparisonResult {

	/**
	 * Constructs the result.
	 * 
	 * @param matchQuality
	 * @param reverseMatchQuality
	 */
	public AbstractPeakComparisonResult(float matchFactor, float reverseMatchFactor) {

		super(matchFactor, reverseMatchFactor);
	}

	public AbstractPeakComparisonResult(float matchFactor, float reverseMatchFactor, float probability) {

		super(matchFactor, reverseMatchFactor, probability);
	}
}
