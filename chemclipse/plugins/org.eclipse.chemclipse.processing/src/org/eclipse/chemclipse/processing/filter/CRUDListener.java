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
package org.eclipse.chemclipse.processing.filter;

public interface CRUDListener<T> {

	/**
	 * This method is called whenever an item needs to be created. The created item might then be modified and passed to {@link #update(Object)} but not to {@link #read(Object)}
	 * 
	 * @param item
	 *            the item that is created or <code>null</code> if create is not supported
	 */
	default T create() {

		return null;
	}

	/**
	 * called when the given item is processed
	 * 
	 * @param item
	 */
	default void read(T item) {

	}

	/**
	 * called whenever an item is updated
	 * 
	 * @param item
	 */
	default void update(T item) {

	}

	/**
	 * called when an item is deleted
	 * 
	 * @param item
	 */
	void delete(T item);
}
