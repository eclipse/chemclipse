/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

/**
 * This class is used to handle match constraints.
 * To compare e.g. mass spectra is time consuming. Hence,
 * the goal is to reduce the number of required matches.
 * Matching spectra will be performed in the following order:
 * --
 * Match Factor (MF)
 * Reverse Match Factor (RMF)
 * Match Factor Direct (MFD)
 * Reverse Match Factor Direct (RMFD)
 * --
 * If the MF is lower than the minimum constraint, then RMF, MFD and RMFD could be skipped.
 *
 */
public class MatchConstraints {

	/*
	 * Values should be used between 0 and 1.
	 */
	private float minMatchFactor = 0.0f;
	private float minMatchFactorDirect = 0.0f;
	private float minReverseMatchFactor = 0.0f;
	private float minReverseMatchFactorDirect = 0.0f;

	/**
	 * All constraints are set to 0, so all matches will be executed.
	 */
	public MatchConstraints() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}

	/**
	 * Set values between 0 and 1.
	 * 
	 * @param minMatchFactor
	 * @param minReverseMatchFactor
	 */
	public MatchConstraints(float minMatchFactor, float minReverseMatchFactor) {
		this(minMatchFactor, 0.0f, minReverseMatchFactor, 0.0f);
	}

	/**
	 * Set values between 0 and 1.
	 * 
	 * @param minMatchFactor
	 * @param minMatchFactorDirect
	 * @param minReverseMatchFactor
	 * @param minReverseMatchFactorDirect
	 */
	public MatchConstraints(float minMatchFactor, float minMatchFactorDirect, float minReverseMatchFactor, float minReverseMatchFactorDirect) {
		this.minMatchFactor = minMatchFactor;
		this.minMatchFactorDirect = minMatchFactorDirect;
		this.minReverseMatchFactor = minReverseMatchFactor;
		this.minReverseMatchFactorDirect = minReverseMatchFactorDirect;
	}

	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	public float getMinMatchFactorDirect() {

		return minMatchFactorDirect;
	}

	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	public float getMinReverseMatchFactorDirect() {

		return minReverseMatchFactorDirect;
	}
}
