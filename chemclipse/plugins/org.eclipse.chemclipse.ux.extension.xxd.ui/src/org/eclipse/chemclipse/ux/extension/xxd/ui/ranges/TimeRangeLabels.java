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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

public class TimeRangeLabels {

	public static final String DELIMITER = "|";
	private static final String REPLACEMENT = "";
	//
	private String title;
	private String initialValue;
	private String[] proposals;
	//
	private String addMessage;
	private String addError;
	private String errorDelimiter;
	private String addExists;
	private String createMessage;
	private String createInitialValue;
	private String editMessage;
	private String deleteMessage;
	private String clearMessage;
	private String resetMessage;

	public TimeRangeLabels() {

		this("Time Range", "C10", new String[]{"C10", "C11", "C12"});
	}

	/**
	 * Create a new label. Use e.g.
	 * title (singular) - "Time Range"
	 * initialValue - "C10"
	 * 
	 * @param title
	 * @param initialValue
	 */
	public TimeRangeLabels(String title, String initialValue) {

		this(title, initialValue, new String[]{initialValue});
	}

	/**
	 * Create a new label. Use e.g.
	 * title (singular) - "Time Range"
	 * initialValue - "C10"
	 * proposals - "C10", "C11", "C12"
	 * 
	 * @param title
	 * @param initialValue
	 */
	public TimeRangeLabels(String title, String initialValue, String[] proposals) {

		this.title = title;
		this.initialValue = replaceDelimiter(initialValue);
		this.proposals = proposals == null ? new String[0] : proposals;
		for(int i = 0; i < this.proposals.length; i++) {
			this.proposals[i] = replaceDelimiter(this.proposals[i]);
		}
		//
		addMessage = String.format("Add a new %s.", title);
		addError = String.format("Please define a new %s.", title);
		errorDelimiter = String.format("The %s must not contain the following delimiter '%s'.", title, DELIMITER);
		addExists = String.format("The %s exists already.", title);
		createMessage = String.format("Create a new %s.", title);
		createInitialValue = String.format("%s | 10.2 | 10.4 | 10.6", this.initialValue);
		editMessage = String.format("Edit the selected %s.", title);
		deleteMessage = String.format("Would you like to delete the selected %s?", title);
		clearMessage = String.format("Would you like to delete the all %ss?", title);
		resetMessage = String.format("Would you like to reset the %ss?", title);
	}

	public String getTitle() {

		return title;
	}

	public String getInitialValue() {

		return initialValue;
	}

	public String[] getProposals() {

		return proposals;
	}

	public String getAddMessage() {

		return addMessage;
	}

	public String getAddInitialValue() {

		return initialValue;
	}

	public String getAddError() {

		return addError;
	}

	public String getErrorDelimiter() {

		return errorDelimiter;
	}

	public String getAddExists() {

		return addExists;
	}

	public String getCreateMessage() {

		return createMessage;
	}

	public String getCreateInitialValue() {

		return createInitialValue;
	}

	public String getEditMessage() {

		return editMessage;
	}

	public String getDeleteMessage() {

		return deleteMessage;
	}

	public String getClearMessage() {

		return clearMessage;
	}

	public String getResetMessage() {

		return resetMessage;
	}

	private String replaceDelimiter(String value) {

		return value.replace(DELIMITER, REPLACEMENT);
	}
}