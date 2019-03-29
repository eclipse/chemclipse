/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.Date;

public interface IProcessingMessage {

	/**
	 * Returns the message type.
	 * 
	 * @return {@link MessageType}
	 */
	MessageType getMessageType();

	/**
	 * Returns the creation date.
	 * 
	 * @return Date
	 */
	Date getDate();

	/**
	 * Get the description.
	 * 
	 * @return String
	 */
	String getDescription();

	/**
	 * Get the detailed message.
	 * 
	 * @return String
	 */
	String getMessage();

	/**
	 * Set a proposed solution.
	 * 
	 * @param proposedSolution
	 */
	void setProposedSolution(String proposedSolution);

	/**
	 * Get the proposed solution.
	 * 
	 * @return
	 */
	String getProposedSolution();

	Throwable getException();

	/**
	 * 
	 * @return the details for this message or <code>null</code> if none are supplied
	 */
	default String getDetails() {

		return null;
	}
}
