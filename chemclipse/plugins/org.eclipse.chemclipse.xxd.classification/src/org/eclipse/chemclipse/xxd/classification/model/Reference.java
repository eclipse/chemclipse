/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.support.text.ILabel;

public enum Reference implements ILabel {

	NAME("Name"), //
	CAS("CAS"), //
	REFERENCE_ID("Reference Id"); //

	private String label;

	private Reference(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}

	public static String[] getItems() {

		return Arrays.stream(Reference.values()).map(Enum::name).collect(Collectors.toList()).toArray(new String[Reference.values().length]);
	}
}