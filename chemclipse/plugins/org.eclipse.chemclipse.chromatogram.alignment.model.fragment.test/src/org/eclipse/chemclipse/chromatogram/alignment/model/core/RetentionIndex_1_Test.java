/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.alignment.model.core.RetentionIndex;
import junit.framework.TestCase;

/**
 * Testing RetentionIndex
 * 
 * @author eselmeister
 */
public class RetentionIndex_1_Test extends TestCase {

	private RetentionIndex retentionIndex;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		retentionIndex = new RetentionIndex();
	}

	@Override
	protected void tearDown() throws Exception {

		retentionIndex = null;
		super.tearDown();
	}

	public void testRetentionTime_1() {

		retentionIndex.setRetentionTime(7503);
		assertEquals("RetentionTime", 7503, retentionIndex.getRetentionTime());
	}

	public void testRetentionTime_2() {

		retentionIndex.setRetentionTime(0);
		assertEquals("RetentionTime", 0, retentionIndex.getRetentionTime());
	}

	public void testRetentionTime_3() {

		retentionIndex.setRetentionTime(-1);
		assertEquals("RetentionTime", 0, retentionIndex.getRetentionTime());
	}

	public void testRetentionTime_4() {

		retentionIndex.setRetentionTime(Integer.MAX_VALUE);
		assertEquals("RetentionTime", Integer.MAX_VALUE, retentionIndex.getRetentionTime());
	}

	public void testRetentionTime_5() {

		retentionIndex.setRetentionTime(Integer.MIN_VALUE);
		assertEquals("RetentionTime", 0, retentionIndex.getRetentionTime());
	}

	public void testIndex_1() {

		retentionIndex.setIndex(5000.4f);
		assertEquals("Index", 5000.4f, retentionIndex.getIndex());
	}

	public void testIndex_2() {

		retentionIndex.setIndex(0.0f);
		assertEquals("Index", 0.0f, retentionIndex.getIndex());
	}

	public void testIndex_3() {

		retentionIndex.setIndex(-1.0f);
		assertEquals("Index", 0.0f, retentionIndex.getIndex());
	}

	public void testIndex_4() {

		retentionIndex.setIndex(Float.MAX_VALUE);
		assertEquals("Index", Float.MAX_VALUE, retentionIndex.getIndex());
	}

	public void testIndex_5() {

		retentionIndex.setIndex(Float.MIN_VALUE);
		assertEquals("Index", Float.MIN_VALUE, retentionIndex.getIndex());
	}

	public void testIndex_6() {

		retentionIndex.setIndex(Float.NaN);
		assertEquals("Index", 0.0f, retentionIndex.getIndex());
	}

	public void testName_1() {

		retentionIndex.setName("C41");
		assertEquals("Name", "C41", retentionIndex.getName());
	}

	public void testName_2() {

		retentionIndex.setName("");
		assertEquals("Name", "", retentionIndex.getName());
	}
}
