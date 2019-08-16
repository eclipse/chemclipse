/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import junit.framework.TestCase;

import org.eclipse.chemclipse.model.core.IScan;

public class Scan_2_Test extends TestCase {

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

		scan.setTimeSegmentId(2);
		assertEquals(2, scan.getTimeSegmentId());
	}

	public void test_2() {

		scan.setCycleNumber(2);
		assertEquals(2, scan.getCycleNumber());
	}
}
