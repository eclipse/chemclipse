/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.history;

import java.util.Date;

import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;

public class EditInformation implements IEditInformation {

	private Date date;
	private String description;
	private String editor;
	public static final String NO_DESCRIPTION = SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_NO_DESCRIPTION);
	public static final String NO_EDITOR = SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_NOT_AVAILABLE);

	public EditInformation(String description) {
		this(new Date(), description, "");
	}

	public EditInformation(String description, String editor) {
		this(new Date(), description, editor);
	}

	public EditInformation(Date date, String description) {
		this(date, description, "");
	}

	/**
	 * Creates a new EditInformation object from the given date, description and editor.
	 * 
	 * @param date
	 * @param description
	 */
	public EditInformation(Date date, String description, String editor) {
		setDate(date);
		setDescription(description);
		setEditor(editor);
	}

	@Override
	public Date getDate() {

		return date;
	}

	/**
	 * Do not set this method public or protected.
	 * 
	 * @param description
	 */
	private void setDate(Date date) {

		/*
		 * Date must not be null.
		 */
		if(date == null) {
			this.date = new Date();
		} else {
			this.date = date;
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	/**
	 * Do not set this method public or protected.
	 * 
	 * @param description
	 */
	private void setDescription(String description) {

		if(description == null) {
			description = NO_DESCRIPTION;
		}
		this.description = description;
	}

	@Override
	public String getEditor() {

		return editor;
	}

	/**
	 * Do not set this method public or protected.
	 * 
	 * @param editor
	 */
	private void setEditor(String editor) {

		if(editor == null) {
			editor = NO_EDITOR;
		}
		this.editor = editor;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		IEditInformation other = (IEditInformation)obj;
		return getDate().getTime() == other.getDate().getTime() && getDescription().equals(other.getDate()) && getEditor().equals(other.getEditor());
	}

	@Override
	public int hashCode() {

		return 7 * date.hashCode() + 11 * description.hashCode() + 13 * editor.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("date=" + date);
		builder.append(",");
		builder.append("description=" + description);
		builder.append(",");
		builder.append("editor=" + editor);
		builder.append("]");
		return builder.toString();
	}
}
