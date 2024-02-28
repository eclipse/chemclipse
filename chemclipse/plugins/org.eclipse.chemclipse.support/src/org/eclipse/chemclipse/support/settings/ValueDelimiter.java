/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

import org.eclipse.chemclipse.support.text.ILabel;

public enum ValueDelimiter implements ILabel {

	TAB("Tab", "\t"), //
	WHITESPACE("Whitespace", " "), //
	COMMA("Comma", ","), //
	SEMICOLON("Semicolon", ";"), //
	COLON("Colon", ":"), //
	PIPE("Pipe", "|"); //

	private String label = "";
	private String delimiter = "";

	private ValueDelimiter(String label, String delimiter) {

		this.label = label;
		this.delimiter = delimiter;
	}

	@Override
	public String label() {

		return label;
	}

	public String delimiter() {

		return delimiter;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}