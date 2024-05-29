/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.support;

import org.eclipse.chemclipse.support.text.ILabel;

public enum TargetsDeleteOption implements ILabel {

	ALL_TARGETS("All Targets"), //
	UNVERIFIED_TARGETS("Unverified Targets"), //
	EMPTY_SMILES("Targets without SMILES"), //
	PROPERTY_IDENTIFIER("By Property (Identifier)");

	private String label = "";

	private TargetsDeleteOption(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}