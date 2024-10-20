/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import org.eclipse.chemclipse.support.text.ILabel;

public enum ChromatographyType implements ILabel {

	GC("Gas Chromatography"), //
	LC("Liquid Chromatography"), //
	TLC("Thin Layer Chromatography"), //
	SEC("Size Exclusion Chromatography"), //
	IC("Ion Chromatography"), //
	CE("Capillary Electrophoresis"), //
	AC("Affinity Chromatography"); //

	private String label;

	private ChromatographyType(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}
