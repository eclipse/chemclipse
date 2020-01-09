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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessPeaksByAreaFilterAreaTypeSelection {

	/**
	 * select "INTEGRATED_AREA" to filter a peak according to integrated area
	 *
	 * @param INTEGRATED_AREA
	 */
	INTEGRATED_AREA("Filter peak according to INTEGRATED AREA"), //
	/**
	 * select "PERCENTAGE_AREA" to filter a peak according to percentage area
	 *
	 * @param PERCENTAGE_AREA
	 */
	PERCENTAGE_AREA("Filter peak according to PERCENTAGE AREA"); //

	@JsonValue
	private String filterAreaTypeSelection;

	private ProcessPeaksByAreaFilterAreaTypeSelection(String filterAreaTypeSelection){

		this.filterAreaTypeSelection = filterAreaTypeSelection;
	}

	@Override
	public String toString() {

		return filterAreaTypeSelection;
	}
}
