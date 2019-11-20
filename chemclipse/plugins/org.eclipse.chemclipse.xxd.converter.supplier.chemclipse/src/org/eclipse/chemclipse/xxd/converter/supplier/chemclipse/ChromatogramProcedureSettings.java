/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChromatogramProcedureSettings {

	public static final String PARAM_CHROMATOGRAM_NAME = "${chromatogram_name}";
	private static final String DEFAULT_NAME = "Copy of " + PARAM_CHROMATOGRAM_NAME;
	@JsonProperty(value = "Copy Chromatogram Selection Range", defaultValue = "false")
	private boolean copyChromatogramSelectionRange;
	@JsonProperty(value = "Add as Reference to Chromatogram", defaultValue = "false")
	private boolean addAsReferenceChromatogram;
	@JsonProperty(value = "Name of Chromatogram", defaultValue = DEFAULT_NAME)
	private String name = DEFAULT_NAME;

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
