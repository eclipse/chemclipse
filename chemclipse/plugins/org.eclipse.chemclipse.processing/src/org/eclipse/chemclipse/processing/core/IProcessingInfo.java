/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation, Generics
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.List;

import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IProcessingInfo<T> extends MessageConsumer, MessageProvider {

	/**
	 * Adds all message from {@link IProcessingInfo} to this processing info
	 * instance.
	 *
	 * @param processingInfo
	 */
	void addMessages(IProcessingInfo<?> processingInfo);

	/**
	 * Adds a message to the processing info.
	 *
	 * @param processingMessage
	 */
	void addMessage(IProcessingMessage processingMessage);

	@Override
	default void addMessage(String description, String message, String detail, Throwable t, MessageType type) {

		ProcessingMessage msg = new ProcessingMessage(type, description, message);
		msg.setException(t);
		msg.setDetails(detail);
		addMessage(msg);
	}

	/**
	 * Adds a warn message to the processing info.
	 *
	 * @param description
	 * @param message
	 */
	@Override
	void addWarnMessage(String description, String message);

	/**
	 * Adds a warn message to the processing info.
	 *
	 * @param description
	 * @param message
	 * @param proposedSolution
	 */
	void addWarnMessage(String description, String message, String proposedSolution);

	/**
	 * Adds an error message to the processing info.
	 *
	 * @param description
	 * @param message
	 * @param proposedSolution
	 */
	void addErrorMessage(String description, String message, String proposedSolution);

	/**
	 * Returns the list of messages.
	 *
	 * @return the list of messages
	 */
	@Override
	List<IProcessingMessage> getMessages();

	/**
	 * Sets the processing result. Each plug-in knows which instance it expects
	 * as the return.
	 *
	 * @param processingResult
	 */
	void setProcessingResult(T processingResult);

	/**
	 * Returns the processing result.
	 * May return null.
	 *
	 * @return Object
	 */
	T getProcessingResult();

	/**
	 * The expected return type an be defined.
	 *
	 * @deprecated
	 */
	@Deprecated
	<V> V getProcessingResult(Class<V> type) throws TypeCastException;

	/**
	 * Creates a new TypeCastException and adds a IProcessingMessage.
	 *
	 * @deprecated
	 */
	@Deprecated
	TypeCastException createTypeCastException(String description, Class<?> actual, Class<?> expected);
}
