/*******************************************************************************
 * Copyright (c) 2010, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.massspectrum;

import org.eclipse.chemclipse.model.identifier.AbstractComparisonResult;

public abstract class AbstractMassSpectrumComparisonResult extends AbstractComparisonResult implements IMassSpectrumComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -903508479872960637L;

	/**
	 * Constructs the result.
	 * 
	 * @param matchFactor
	 * @param reverseMatchFactor
	 */
	public AbstractMassSpectrumComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect);
	}

	public AbstractMassSpectrumComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
	}
}
