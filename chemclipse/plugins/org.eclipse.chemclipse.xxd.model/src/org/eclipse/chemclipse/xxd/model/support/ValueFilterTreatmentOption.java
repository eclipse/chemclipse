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
 *  Describes how the filter will treat (e.g. process) the peak value with the
 *  selected option:
 *  <li>{@link #ENABLE_PEAK}</li>
 *  <li>{@link #DEACTIVATE_PEAK}</li>
 *  <li>{@link #KEEP_PEAK}</li>
 *  <li>{@link #DELETE_PEAK}</li>
 */
public enum ValueFilterTreatmentOption {

	/**
	 * Select to enable a peak if certain constraints are matched
	 */
	ENABLE_PEAK("Enable peak"), //
	/**
	 * Select to deactivate a peak if certain constraints are matched
	 */
	DEACTIVATE_PEAK("Deactivate peak"), //
	/**
	 * Select to keep a peak if certain constraints are matched
	 */
	KEEP_PEAK("Keep peak"), //
	/**
	 * Select to delete a peak if certain constraints are matched
	 */
	DELETE_PEAK("Delete peak"); //

	@JsonValue
	private String filterTreatmentOption;

	private ValueFilterTreatmentOption(String filterTreatmentOption){

		this.filterTreatmentOption = filterTreatmentOption;
	}

	@Override
	public String toString() {

		return filterTreatmentOption;
	}
}
