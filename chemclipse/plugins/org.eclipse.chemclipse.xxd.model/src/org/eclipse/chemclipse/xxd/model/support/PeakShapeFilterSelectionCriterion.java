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

public enum PeakShapeFilterSelectionCriterion {

	/**
	 * Select peaks whose leading values are too small
	 * 
	 * @param LEADING_SMALLER_THAN_LIMIT
	 */
	LEADING_SMALLER_THAN_LIMIT("Leading < limit"), //
	/**
	 * Select peaks whose tailing values are too large
	 *
	 * @param TAILING_GREATER_THAN_LIMIT
	 */
	TAILING_GREATER_THAN_LIMIT("Tailing > limit"), //
	/**
	 * Select peaks whose values are within the defined range
	 *
	 * @param TAILING_GREATER_THAN_LIMIT
	 */
	VALUES_WITHIN_RANGE("values >= Leading and <= Tailing"); //
	
	@JsonValue
	private String filterSelectionCriterion;

	private PeakShapeFilterSelectionCriterion(String filterSelectionCriterion){

		this.filterSelectionCriterion = filterSelectionCriterion;
	}

	@Override
	public String toString() {

		return filterSelectionCriterion;
	}
}
