/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public class PeakComparisonResult extends AbstractPeakComparisonResult implements IPeakComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1197615645280532178L;
	private boolean unique;

	public PeakComparisonResult(float matchQuality, float reverseMatchQuality, float matchFactorDirect, float reverseMatchFactorDirect) {
		super(matchQuality, reverseMatchQuality, matchFactorDirect, reverseMatchFactorDirect);
	}

	public PeakComparisonResult(float matchFactor, float reverseMatchFactor, float matchFactorDirect, float reverseMatchFactorDirect, float probability) {
		super(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
	}

	@Override
	public boolean isMarkerPeak() {

		return unique;
	}

	@Override
	public void setMarkerPeak(boolean unique) {

		this.unique = unique;
	}
}
