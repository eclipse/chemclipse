/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.TestPathHelper;

public class WindowsSupport_2_ITest extends AbstractBackgroundTestCase {

	private IExtendedRuntimeSupport runtimeSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstruct_1() {

		String nistApp = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_WINDOWS_NIST_APPLICATION);
		try {
			runtimeSupport = new WindowsSupport(new File(nistApp).getParentFile(), parameterBackground);
			assertNotNull(runtimeSupport);
		} catch(FileNotFoundException e) {
			assertTrue("A file not found exception should not occur here.", false);
		}
	}

	public void testConstruct_2() {

		String nistApp = "";
		try {
			runtimeSupport = new WindowsSupport(new File(nistApp).getParentFile(), parameterBackground);
			assertNull(runtimeSupport);
		} catch(FileNotFoundException e) {
			assertTrue("A file not found exception should not occur here.", true);
		}
	}
}
