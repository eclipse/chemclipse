/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

/**
 * A {@link MessageConsumer} consumes messages from a processing or computation method to give feedback to the caller in a very detailed way
 * 
 * @author Christoph Läubrich
 *
 */
public interface MessageConsumer {

	default void addErrorMessage(final String description, final String message) {

		addErrorMessage(description, message, null);
	}

	default void addErrorMessage(final String description, final String message, Throwable t) {

		addMessage(description, message, t, MessageType.ERROR);
	}

	default void addInfoMessage(final String description, final String message, Throwable t) {

		addMessage(description, message, t, MessageType.INFO);
	}

	default void addInfoMessage(final String description, final String message) {

		addInfoMessage(description, message, null);
	}

	default void addWarnMessage(final String description, final String message) {

		addWarnMessage(description, message, null);
	}

	default void addWarnMessage(final String description, final String message, Throwable t) {

		addMessage(description, message, t, MessageType.WARN);
	}

	default void addMessage(final String description, final String message, final MessageType type) {

		addMessage(description, message, null, type);
	}

	void addMessage(final String description, final String message, Throwable t, final MessageType type);
}
