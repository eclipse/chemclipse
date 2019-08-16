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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.chemclipse.keystore.exceptions.NoKeyAvailableException;
import org.eclipse.chemclipse.keystore.internal.support.KeyFileParser;
import org.eclipse.core.runtime.Platform;

public class KeyStore implements IKeyStore {

	private static final String KEYSTORE_FILE_NAME = "keystore";
	private Map<String, String> keyMap;

	public KeyStore() {
		initializeKeyMap();
	}

	@Override
	public TreeSet<String> getRegisteredIds() {

		return new TreeSet<String>(keyMap.keySet());
	}

	@Override
	public String getSerial(String id) throws NoKeyAvailableException {

		/*
		 * Return the key for the given id.
		 * The id is the key in the map and the key is the value.
		 */
		String serial = keyMap.get(id);
		if(serial == null || serial.equals("")) {
			throw new NoKeyAvailableException("The serial for the id " + id + " is not available.");
		}
		return serial;
	}

	@Override
	public boolean containsSerial(String id) {

		return keyMap.containsKey(id);
	}

	@Override
	public void reload() {

		initializeKeyMap();
	}

	private void initializeKeyMap() {

		String path = Platform.getInstallLocation().getURL().getPath();
		File file = new File(path + File.separator + KEYSTORE_FILE_NAME);
		/*
		 * Parse the key file if it exists.
		 */
		if(file.exists()) {
			keyMap = KeyFileParser.readKeysFromFile(file);
		} else {
			keyMap = new HashMap<String, String>();
		}
	}
}
