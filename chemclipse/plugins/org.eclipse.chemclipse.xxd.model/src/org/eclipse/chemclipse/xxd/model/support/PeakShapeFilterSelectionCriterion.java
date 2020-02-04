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
 *  Describes the criterion to select the peak leading or tailing:
 *  <li>{@link #LEADING_SMALLER_THAN_LIMIT}</li>
 *  <li>{@link #TAILING_GREATER_THAN_LIMIT}</li>
 *  <li>{@link #VALUES_WITHIN_RANGE}</li>
 */
public enum PeakShapeFilterSelectionCriterion {

	/**
	 * Select peaks whose leading values smaller than the limit
	 */
	LEADING_SMALLER_THAN_LIMIT("Leading < limit"), //
	/**
	 * Select peaks whose tailing values are larger than the limit
	 */
	TAILING_GREATER_THAN_LIMIT("Tailing > limit"), //
	/**
	 * Select peaks whose values are within the defined range, e.g. greater or
	 * equal to leading and smaller or equal to tailing
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
