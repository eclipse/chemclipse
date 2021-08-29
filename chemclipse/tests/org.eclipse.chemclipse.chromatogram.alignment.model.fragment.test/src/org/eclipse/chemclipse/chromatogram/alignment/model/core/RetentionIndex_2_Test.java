/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.model.core;

import junit.framework.TestCase;

/**
 * Testing RetentionIndex
 * 
 * @author eselmeister
 */
public class RetentionIndex_2_Test extends TestCase {

	private RetentionIndex retentionIndex;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testRetentionTime_1() {

		retentionIndex = new RetentionIndex();
		assertEquals("RetentionTime", 0, retentionIndex.getRetentionTime());
		assertEquals("Index", 0.0f, retentionIndex.getIndex());
		assertEquals("Name", "", retentionIndex.getName());
	}

	public void testRetentionTime_2() {

		retentionIndex = new RetentionIndex(7500, 8000.2f);
		assertEquals("RetentionTime", 7500, retentionIndex.getRetentionTime());
		assertEquals("Index", 8000.2f, retentionIndex.getIndex());
		assertEquals("Name", "", retentionIndex.getName());
	}

	public void testRetentionTime_3() {

		retentionIndex = new RetentionIndex(7500, 8000.2f, "C41");
		assertEquals("RetentionTime", 7500, retentionIndex.getRetentionTime());
		assertEquals("Index", 8000.2f, retentionIndex.getIndex());
		assertEquals("Name", "C41", retentionIndex.getName());
	}

	public void testRetentionTime_4() {

		retentionIndex = new RetentionIndex(-7500, -8000.2f, "C41");
		assertEquals("RetentionTime", 0, retentionIndex.getRetentionTime());
		assertEquals("Index", 0.0f, retentionIndex.getIndex());
		assertEquals("Name", "C41", retentionIndex.getName());
	}
}
