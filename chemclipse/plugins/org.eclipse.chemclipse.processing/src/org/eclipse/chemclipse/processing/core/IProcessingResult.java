/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 * Lorenz Gerber - add additional message field
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.List;

public interface IProcessingResult<T> extends MessageConsumer, MessageProvider {

	void addMessage(IProcessingMessage processingMessage);

	default void addMessages(final IProcessingResult<?> processingInfo) {

		for(final IProcessingMessage message : processingInfo.getMessages()) {
			addMessage(message);
		}
	}

	@Override
	default void addMessage(String description, String message, String detail, String solution, Throwable t, MessageType type) {

		ProcessingMessage msg = new ProcessingMessage(type, description, message, solution);
		if(detail != null) {
			msg.setDetails(detail);
		}
		msg.setException(t);
		addMessage(msg);
	}

	@Override
	List<IProcessingMessage> getMessages();

	List<IProcessingMessage> getMessages(MessageType type);

	T getProcessingResult();

	void setProcessingResult(T processingResult);

	IProcessingInfo<T> toInfo();
}
