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

public enum ProcessPeaksByPeakWidthFilterSelectionCriterion {

	/**
	 * select "WIDTH_SMALLER_THAN_LIMIT" to select peaks whose width is too small
	 * 
	 * @param WIDTH_SMALLER_THAN_LIMIT
	 */
	WIDTH_SMALLER_THAN_LIMIT("Peak width value is SMALLER THAN LIMIT"), //
	/**
	 * select "WIDTH_GREATER_THAN_LIMIT" to select peaks whose width is too large
	 *
	 * @param WIDTH_GREATER_THAN_LIMIT
	 */
	WIDTH_GREATER_THAN_LIMIT("Peak width value is GREATER THAN LIMIT"); //	

	@JsonValue
	private String filterSelectionCriterion;

	private ProcessPeaksByPeakWidthFilterSelectionCriterion(String filterSelectionCriterion){

		this.filterSelectionCriterion = filterSelectionCriterion;
	}

	@Override
	public String toString() {

		return filterSelectionCriterion;
	}
}
