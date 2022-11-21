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

	private String title = "Time Range";
	private String initialValue = "C10";
	//
	private String addMessage;
	private String addError;
	private String addExists;
	private String createMessage;
	private String createInitialValue;
	private String editMessage;
	private String deleteMessage;
	private String clearMessage;
	private String resetMessage;

	public TimeRangeLabels() {

		this("Time Range", "C10");
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

		this.title = title;
		this.initialValue = initialValue.replace("|", "");
		//
		addMessage = String.format("Add a new %s.", title);
		addError = String.format("Please define a new %s.", title);
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

	public String getAddMessage() {

		return addMessage;
	}

	public String getAddInitialValue() {

		return initialValue;
	}

	public String getAddError() {

		return addError;
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
}