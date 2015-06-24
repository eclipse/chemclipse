/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.history;

import java.util.Date;

/**
 * EditInformation stores descriptions of the steps where the chromatogram has
 * been edited.
 * 
 * @author eselmeister
 */
public class EditInformation implements IEditInformation {

	private Date date;
	private String description;
	public static final String NO_DESCRIPTION = "No description available - please describe the edit process.";

	/**
	 * Creates a new description object and sets the actual date to it.
	 * 
	 * @param description
	 */
	public EditInformation(String description) {

		/*
		 * Date must not be null.
		 */
		this.date = new Date();
		setDescription(description);
	}

	/**
	 * Creates a new EditInformation object from the given date and description.
	 * 
	 * @param date
	 * @param description
	 */
	public EditInformation(Date date, String description) {

		setDate(date);
		setDescription(description);
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
			date = new Date();
		}
		this.date = new Date();
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
	// -----------------------------------hashCode, equals, toString
	// TODO implementieren hashCode, equals, toString
	// -----------------------------------hashCode, equals, toString
}
