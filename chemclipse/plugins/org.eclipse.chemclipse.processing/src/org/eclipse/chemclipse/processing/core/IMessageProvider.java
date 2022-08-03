/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import java.util.Collection;

/**
 * A {@link IMessageProvider} can provide Messages, this is the counterpart for {@link IMessageConsumer}
 * 
 * @author Christoph Läubrich
 *
 */
public interface IMessageProvider {

	/**
	 * Returns the list of messages.
	 *
	 * @return the list of messages
	 */
	Collection<? extends IProcessingMessage> getMessages();

	/**
	 * Returns whether the process info stores error message or not.
	 *
	 * @return boolean
	 */
	default boolean hasErrorMessages() {

		return hasMessages(MessageType.ERROR);
	}

	/**
	 * Returns whether the process info stores warn message or not.
	 *
	 * @return boolean
	 */
	default boolean hasWarnMessages() {

		return hasMessages(MessageType.WARN);
	}

	default boolean hasMessages(final MessageType type) {

		for(IProcessingMessage message : getMessages()) {
			if(message.getMessageType() == type) {
				return true;
			}
		}
		return false;
	}
}
