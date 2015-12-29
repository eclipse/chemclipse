/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public abstract class AbstractProcessingInfo implements IProcessingInfo {

	private List<IProcessingMessage> processingMessages;
	private Object processingResult;

	public AbstractProcessingInfo() {
		processingMessages = new ArrayList<IProcessingMessage>();
	}

	public AbstractProcessingInfo(IProcessingInfo processingInfo) {
		this();
		addMessages(processingInfo);
	}

	@Override
	public void addMessages(IProcessingInfo processingInfo) {

		if(processingInfo != null) {
			/*
			 * Add each message to this message queue.
			 */
			for(IProcessingMessage message : processingInfo.getMessages()) {
				addMessage(message);
			}
		}
	}

	@Override
	public void addMessage(IProcessingMessage processingMessage) {

		processingMessages.add(processingMessage);
	}

	@Override
	public void addInfoMessage(String description, String message) {

		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.INFO, description, message);
		addMessage(processingMessage);
	}

	@Override
	public void addWarnMessage(String description, String message) {

		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.WARN, description, message);
		addMessage(processingMessage);
	}

	@Override
	public void addErrorMessage(String description, String message) {

		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, description, message);
		addMessage(processingMessage);
	}

	@Override
	public List<IProcessingMessage> getMessages() {

		return processingMessages;
	}

	@Override
	public void setProcessingResult(Object processingResult) {

		this.processingResult = processingResult;
	}

	@Override
	public Object getProcessingResult() {

		return processingResult;
	}

	@Override
	public boolean hasErrorMessages() {

		for(IProcessingMessage processingMessage : processingMessages) {
			if(processingMessage.getMessageType().equals(MessageType.ERROR)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasWarnMessages() {

		for(IProcessingMessage processingMessage : processingMessages) {
			if(processingMessage.getMessageType().equals(MessageType.WARN)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public TypeCastException createTypeCastException(String description, Class<?> clazz) {

		String message = "The object couldn't be casted to: " + clazz;
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, description, message);
		addMessage(processingMessage);
		return new TypeCastException(message);
	}
}
