/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.support;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  Describes the criterion to select the peak asymmetry factor:
 *  <li>{@link #ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT}</li>
 *  <li>{@link #ASYMMETRY_FACTOR_GREATER_THAN_LIMIT}</li>
 */
public enum PeakAsymmetryFilterSelectionCriterion {

	/**
	 * Select peaks whose peak asymmetry factor is too small
	 */
	ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT("As < asym. factor"), //	
	/**
	 * Select peaks whose peak asymmetry factor is too large
	 */
	ASYMMETRY_FACTOR_GREATER_THAN_LIMIT("As > asym. factor"); //	

	@JsonValue
	private String filterSelectionCriterion;

	private PeakAsymmetryFilterSelectionCriterion(String filterSelectionCriterion){

		this.filterSelectionCriterion = filterSelectionCriterion;
	}

	@Override
	public String toString() {

		return filterSelectionCriterion;
	}
}
