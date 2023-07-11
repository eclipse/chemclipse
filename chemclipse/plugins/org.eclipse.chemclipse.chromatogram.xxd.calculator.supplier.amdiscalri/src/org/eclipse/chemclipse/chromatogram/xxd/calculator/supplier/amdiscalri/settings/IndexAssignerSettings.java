/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexAssigner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class IndexAssignerSettings {

	@JsonProperty(value = "Retention Index Assigner", defaultValue = "")
	@JsonPropertyDescription("The list of retention index mappings.")
	private RetentionIndexAssigner retentionIndexAssigner = new RetentionIndexAssigner();

	public RetentionIndexAssigner getRetentionIndexAssigner() {

		return retentionIndexAssigner;
	}

	public void setRetentionIndexAssigner(RetentionIndexAssigner retentionIndexAssigner) {

		this.retentionIndexAssigner = retentionIndexAssigner;
	}
}