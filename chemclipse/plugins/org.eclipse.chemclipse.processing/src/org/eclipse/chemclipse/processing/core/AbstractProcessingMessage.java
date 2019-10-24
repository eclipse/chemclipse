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

public abstract class AbstractProcessingMessage implements IProcessingMessage {

	private MessageType messageType = MessageType.ERROR;
	private Date date;
	private String description = "Description was null.";
	private String message = "Message was null.";
	private String proposedSolution = "";
	private String details;

	public AbstractProcessingMessage(MessageType messageType, String description, String message) {
		this(messageType, description, message, "");
	}

	public AbstractProcessingMessage(MessageType messageType, String description, String message, String proposedSolution) {
		/*
		 * New Date.
		 */
		date = new Date();
		/*
		 * MessageType
		 */
		if(messageType != null) {
			this.messageType = messageType;
		}
		/*
		 * Description
		 */
		if(description != null) {
			this.description = description;
		}
		/*
		 * Message
		 */
		if(message != null) {
			this.message = message;
		}
		/*
		 * Proposed Solution
		 */
		if(proposedSolution != null) {
			this.proposedSolution = proposedSolution;
		}
	}

	@Override
	public MessageType getMessageType() {

		return messageType;
	}

	@Override
	public Date getDate() {

		return date;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getMessage() {

		return message;
	}

	@Override
	public void setProposedSolution(String proposedSolution) {

		this.proposedSolution = proposedSolution;
	}

	@Override
	public String getProposedSolution() {

		return proposedSolution;
	}

	@Override
	public Throwable getException() {

		return null;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("date=" + date);
		builder.append(",");
		builder.append("messageType=" + messageType);
		builder.append(",");
		builder.append("description=" + description);
		builder.append(",");
		builder.append("message=" + message);
		builder.append(",");
		builder.append("proposedSolution=" + proposedSolution);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String getDetails() {

		return details;
	}

	public void setDetails(String details) {

		this.details = details;
	}
}
