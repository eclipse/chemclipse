/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

public class DeleteQuantitationsFilterSettings {

	@JsonProperty(value = "Delete Quantitations", defaultValue = "false")
	@JsonPropertyDescription(value = "Confirm to delete the quantitations.")
	private boolean deleteQuantitations;

	public boolean isDeleteQuantitations() {

		return deleteQuantitations;
	}

	public void setDeleteQuantitations(boolean deleteQuantitations) {

		this.deleteQuantitations = deleteQuantitations;
	}
}