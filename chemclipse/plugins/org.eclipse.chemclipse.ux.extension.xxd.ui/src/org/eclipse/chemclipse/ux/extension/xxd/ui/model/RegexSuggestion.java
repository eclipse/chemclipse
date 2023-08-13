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
package org.eclipse.chemclipse.ux.extension.xxd.ui.model;

import org.eclipse.chemclipse.support.text.ILabel;

public enum RegexSuggestion implements ILabel {

	GROUP("Group", "()"), //
	MARKER_OPTIONAL("Marker (optional", "?"), //
	MARKER_MANDATORY("Marker (mandatory)", "+"), //
	MARKER_MATCH_ALL("Marker (match all)", ".*"), //
	CHARACTER("Character (a)", "\\w"), //
	WHITESPACE_FIX("Whitespace (fix)", "\\s"), //
	WHITESPACE_OPTIONAL("Whitespace (optional)", "\\s?"), //
	DIGIT_SIMPLE("Digit ('5')", "\\d+"), //
	DIGIT_DECIMAL("Digit ('5.676')", "\\d+\\.\\d+"), //
	DIGIT_ANY("Digit ('5' or '5.676')", "\\d+\\.?\\d*"), //
	CHOICE("Choice (either a or b)", "[a|b]"); //

	private String label = "";
	private String command = "";

	private RegexSuggestion(String label, String command) {

		this.label = label;
		this.command = command;
	}

	public String label() {

		return label;
	}

	public String command() {

		return command;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}