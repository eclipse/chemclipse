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

public enum ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion {

	/**
	 * select "LEADING_GREATER_THAN_LIMIT" to select peaks whose leading values are too large
	 * 
	 * @param LEADING_GREATER_THAN_LIMIT
	 */
	LEADING_GREATER_THAN_LIMIT("Peak leading value is GREATER THAN LIMIT"), //
	/**
	 * select "TAILING_GREATER_THAN_LIMIT" to select peaks whose tailing values are too large
	 *
	 * @param TAILING_GREATER_THAN_LIMIT
	 */
	TAILING_GREATER_THAN_LIMIT("Peak tailing value is GREATER THAN LIMIT"), //
	/**
	 * select "PEAK_ASYMMETRY_FACTOR_TOO_LARGE" to select peaks whose peak asymmetry factor
	 * is too large
	 *
	 * @param PEAK_ASYMMETRY_FACTOR_TOO_LARGE
	 */
	PEAK_ASYMMETRY_FACTOR_GREATER_THAN_LIMIT("Peak asymmetry factor is GREATER THAN LIMIT"); //	

	@JsonValue
	private String filterSelectionCriterion;

	private ProcessPeaksByAsymmetricalPeakShapeFilterSelectionCriterion(String filterSelectionCriterion){

		this.filterSelectionCriterion = filterSelectionCriterion;
	}

	@Override
	public String toString() {

		return filterSelectionCriterion;
	}
}
