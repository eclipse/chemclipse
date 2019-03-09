/*******************************************************************************
 * Copyright (c) 2017, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.List;

public interface IProcessingResult<T> {

	default void addErrorMessage(final String description, final String message) {

		addMessage(description, message, MessageType.ERROR);
	}

	default void addInfoMessage(final String description, final String message) {

		final IProcessingMessage processingMessage = new ProcessingMessage(MessageType.INFO, description, message);
		addMessage(processingMessage);
	}

	void addMessage(IProcessingMessage processingMessage);

	default void addMessage(final String description, final String message, final MessageType type) {

		addMessage(new ProcessingMessage(type, description, message));
	}

	default void addMessages(final IProcessingResult<?> processingInfo) {

		for(final IProcessingMessage message : processingInfo.getMessages()) {
			addMessage(message);
		}
	}

	default void addWarnMessage(final String description, final String message) {

		addMessage(description, message, MessageType.WARN);
	}

	List<IProcessingMessage> getMessages();

	List<IProcessingMessage> getMessages(MessageType type);

	T getProcessingResult();

	default boolean hasErrorMessages() {

		return !getMessages(MessageType.ERROR).isEmpty();
	}

	default boolean hasMessages(final MessageType type) {

		return !getMessages(type).isEmpty();
	}

	default boolean hasWarnMessages() {

		return !getMessages(MessageType.WARN).isEmpty();
	}

	void setProcessingResult(T processingResult);

	IProcessingInfo<T> toInfo();
}
