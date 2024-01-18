/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import junit.framework.TestCase;

public class RetentionIndexMap_1_Test extends TestCase {

	private RetentionIndexMap retentionIndexMap = new RetentionIndexMap();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(-1, retentionIndexMap.getRetentionTime(1000));
	}

	public void test2() {

		assertEquals(0.0f, retentionIndexMap.getRetentionIndex(1000));
	}

	public void test3() {

		assertTrue(retentionIndexMap.isEmpty());
	}

	public void test4() {

		assertEquals(-1, retentionIndexMap.getRetentionIndexStart());
	}

	public void test5() {

		assertEquals(-1, retentionIndexMap.getRetentionIndexStop());
	}
}