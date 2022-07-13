/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.model;

import org.eclipse.chemclipse.support.text.ILabel;

public enum DecimalSeparator implements ILabel { // TODO move

	COMMA(',', "Comma"), //
	DOT('.', "Dot");

	private char character;
	private String label;

	private DecimalSeparator(char character, String label) {

		this.character = character;
		this.label = label;
	}

	public char getCharacter() {

		return character;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}