/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - update version
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse;

import org.eclipse.chemclipse.model.settings.IProcessSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChromatogramProcedureSettings implements IProcessSettings {

	@JsonProperty(value = "Copy Chromatogram Selection Range", defaultValue = "false")
	private boolean copyChromatogramSelectionRange;
	@JsonProperty(value = "Add as Reference to Chromatogram", defaultValue = "false")
	private boolean addAsReferenceChromatogram;
	@JsonProperty(value = "Chromatogram Name", defaultValue = VARIABLE_CHROMATOGRAM_NAME)
	private String name = VARIABLE_CHROMATOGRAM_NAME;

	public boolean isCopyChromatogramSelectionRange() {

		return copyChromatogramSelectionRange;
	}

	public void setCopyChromatogramSelectionRange(boolean copyChromatogramSelectionRange) {

		this.copyChromatogramSelectionRange = copyChromatogramSelectionRange;
	}

	public boolean isAddAsReferenceChromatogram() {

		return addAsReferenceChromatogram;
	}

	public void setAddAsReferenceChromatogram(boolean addAsReferenceChromatogram) {

		this.addAsReferenceChromatogram = addAsReferenceChromatogram;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}
}