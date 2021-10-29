/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings.peaks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeleteStandardsFilterSettings {

	@JsonProperty(value = "Delete Standard(s)", defaultValue = "false")
	@JsonPropertyDescription(value = "Confirm to delete the standard(s).")
	private boolean deleteStandards;

	public boolean isDeleteStandards() {

		return deleteStandards;
	}

	public void setDeleteStandards(boolean deleteStandards) {

		this.deleteStandards = deleteStandards;
	}
}