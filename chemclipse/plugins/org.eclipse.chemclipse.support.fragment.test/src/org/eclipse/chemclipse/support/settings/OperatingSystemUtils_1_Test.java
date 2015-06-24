/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

import junit.framework.TestCase;

public class OperatingSystemUtils_1_Test extends TestCase {

	private IOperatingSystemUtils operatingSystemUtils;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		operatingSystemUtils = new OperatingSystemUtils();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetLineDelimiter_1() {

		String delimiter = operatingSystemUtils.getLineDelimiter();
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("windows")) {
			assertEquals("\r\n", delimiter);
		} else if(os.startsWith("mac")) {
			assertEquals("\r", delimiter);
		} else if(os.startsWith("unix")) {
			assertEquals("\n", delimiter);
		} else if(os.startsWith("linux")) {
			assertEquals("\n", delimiter);
		} else {
			assertEquals("\r\n", delimiter);
		}
	}
}
