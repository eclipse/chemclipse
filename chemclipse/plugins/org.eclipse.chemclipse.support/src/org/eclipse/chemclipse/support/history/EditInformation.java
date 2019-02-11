/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
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
	private String description = "";
	private String editor = "";
	//
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
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((editor == null) ? 0 : editor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		EditInformation other = (EditInformation)obj;
		if(date == null) {
			if(other.date != null)
				return false;
		} else if(!date.equals(other.date))
			return false;
		if(description == null) {
			if(other.description != null)
				return false;
		} else if(!description.equals(other.description))
			return false;
		if(editor == null) {
			if(other.editor != null)
				return false;
		} else if(!editor.equals(other.editor))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "EditInformation [date=" + date + ", description=" + description + ", editor=" + editor + "]";
	}
}
