/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.massspectrum;

public class MassSpectrumComparisonResult extends AbstractMassSpectrumComparisonResult implements IMassSpectrumComparisonResult {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -5698918504463648333L;

	public MassSpectrumComparisonResult(float matchFactor, float reverseMatchFactor, float probability) {
		super(matchFactor, reverseMatchFactor, probability);
	}

	public MassSpectrumComparisonResult(float matchFactor, float reverseMatchFactor) {
		super(matchFactor, reverseMatchFactor);
	}
}
