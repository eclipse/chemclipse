/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IScan;

import junit.framework.TestCase;

public class Scan_6_Test extends TestCase {

	private IScan scan;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scan = new Scan(1000.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		scan = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals(0, scan.getRelativeRetentionTime());
	}

	public void test_2() {

		scan.setRelativeRetentionTime(-1);
		assertEquals(0, scan.getRelativeRetentionTime());
	}

	public void test_3() {

		scan.setRelativeRetentionTime(0);
		assertEquals(0, scan.getRelativeRetentionTime());
	}

	public void test_4() {

		scan.setRelativeRetentionTime(1);
		assertEquals(1, scan.getRelativeRetentionTime());
	}
}
