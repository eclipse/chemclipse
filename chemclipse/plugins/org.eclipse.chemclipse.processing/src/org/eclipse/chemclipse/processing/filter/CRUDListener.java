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

import java.util.Collection;
import java.util.Collections;

/**
 * Provides CRUD-similar functionality for filter types with the following contracts
 * <ol>
 * <li>read might or might not reflect items that where added/deleted while access to that object was given to the caller</li>
 * <li>create might not be supported in which case UnsupportedOperationException is thrown</li>
 * <li>update only notifies that an object previously obtained through create or read was modified
 * <li>delete requests a delete, but this might be ignored
 * </ol>
 * 
 * @author christoph
 *
 * @param <ItemType>
 *            the type of items this {@link CRUDListener} applies to
 * @param <CreationContext>
 *            the context used to create new items
 */
public interface CRUDListener<ItemType, CreationContext> {

	/**
	 * This method is called whenever an item needs to be created. The created item might then be modified further and passed to {@link #update(Object)}
	 * 
	 * @param item
	 *            the item that is created
	 * @throws UnsupportedOperationException
	 *             when create is not supported
	 */
	default ItemType create(CreationContext context) throws UnsupportedOperationException {

		throw new UnsupportedOperationException();
	}

	/**
	 * reads the current Collection of items
	 * 
	 * @param item
	 */
	default Collection<ItemType> read() {

		return Collections.emptyList();
	}

	/**
	 * called whenever an item was updated
	 * 
	 */
	default void updated(ItemType item) {

	}

	/**
	 * called when an item should be deleted
	 * 
	 * @param item
	 * @return <code>true</code> if item actually was deleted <code>false</code> otherwise
	 */
	default boolean delete(ItemType item) {

		return false;
	}
}
