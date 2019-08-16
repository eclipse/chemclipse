/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.keystore.core;

import java.util.TreeSet;

import org.eclipse.chemclipse.keystore.exceptions.NoKeyAvailableException;

/**
 * The key store serves keys if available.
 * It is possible to store a key file in the root folder of the application.
 * If it exists, the key store will parse the entries and serves them on request.
 * 
 * @author Dr. Philip Wenig
 * 
 */
public interface IKeyStore {

	/**
	 * Returns the sorted set of registered keys.
	 * 
	 * @return Set<String>
	 */
	TreeSet<String> getRegisteredIds();

	/**
	 * This method returns the serial to the given id.
	 * An exception will be thrown if no key is available.
	 * 
	 * @param id
	 * @return String
	 * @throws NoKeyAvailableException
	 */
	String getSerial(String id) throws NoKeyAvailableException;

	/**
	 * Returns whether the serial is available or not.
	 * 
	 * @param id
	 * @return boolean
	 */
	boolean containsSerial(String id);

	/**
	 * Reloads the key store.
	 */
	void reload();
}
