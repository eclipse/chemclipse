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

public enum ProcessPeaksByAreaFilterTreatmentOption {

	/**
	 * select "DISABLE_PEAK" to disable a peak if certain area constraints are
	 * matched
	 *
	 * @param DISABLE_PEAK
	 */
	DISABLE_PEAK("DISABLE PEAK if following criterion is matched"), //
	/**
	 * select "DELETE_PEAK" to delete a peak if certain area constraints are matched
	 *
	 * @param DELETE_PEAK
	 */
	DELETE_PEAK("DELETE PEAK if following criterion is matched"); //

	@JsonValue
	private String filterTreatmentOption;

	private ProcessPeaksByAreaFilterTreatmentOption(String filterTreatmentOption){

		this.filterTreatmentOption = filterTreatmentOption;
	}

	@Override
	public String toString() {

		return filterTreatmentOption;
	}
}
