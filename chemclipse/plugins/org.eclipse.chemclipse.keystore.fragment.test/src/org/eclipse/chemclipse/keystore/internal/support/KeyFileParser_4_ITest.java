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
package org.eclipse.chemclipse.keystore.internal.support;

import java.util.Map;

import junit.framework.TestCase;

public class KeyFileParser_4_ITest extends TestCase {

	private Map<String, String> keyStore;

	@Override
	protected void setUp() throws Exception {

		keyStore = KeyFileParser.readKeysFromFile(null);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testAmount() {

		assertEquals(0, keyStore.size());
	}

	public void testContainsKey_1() {

		assertFalse(keyStore.containsKey("mykeyid"));
	}

	public void testGetKey_1() {

		assertNull(keyStore.get("mykeyid"));
	}
}
