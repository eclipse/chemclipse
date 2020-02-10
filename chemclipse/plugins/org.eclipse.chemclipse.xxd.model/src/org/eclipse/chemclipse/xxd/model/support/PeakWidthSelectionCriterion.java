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

public enum PeakWidthSelectionCriterion {

	/**
	 * Select peaks whose width is smaller than the limit
	 * 
	 * @param WIDTH_SMALLER_THAN_LIMIT
	 */
	WIDTH_SMALLER_THAN_LIMIT("Width < limit"), //
	/**
	 * Select peaks whose width is greater than the limit
	 *
	 * @param WIDTH_GREATER_THAN_LIMIT
	 */
	WIDTH_GREATER_THAN_LIMIT("Width > limit"); //	

	@JsonValue
	private String filterSelectionCriterion;

	private PeakWidthSelectionCriterion(String filterSelectionCriterion){

		this.filterSelectionCriterion = filterSelectionCriterion;
	}

	@Override
	public String toString() {

		return filterSelectionCriterion;
	}
}