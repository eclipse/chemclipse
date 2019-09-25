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
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

public interface ObjectChangedListener<T> {

	enum ChangeType {
		CREATED, CHANGED, DELETED, SELECTED
	}

	/**
	 * notifies the listener about a change in the object state
	 * 
	 * @param type
	 *            the type of change
	 * @param newObject
	 *            the new object (might be <code>null</code> e.g in case of {@link ChangeType#DELETED})
	 * @param oldObject
	 *            the old object (might be <code>null</code> e.g in case of {@link ChangeType#CREATED})
	 */
	void objectChanged(ChangeType type, T newObject, T oldObject);
}
