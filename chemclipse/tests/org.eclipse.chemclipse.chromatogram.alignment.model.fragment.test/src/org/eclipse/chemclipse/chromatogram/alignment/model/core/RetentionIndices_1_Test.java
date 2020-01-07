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
import org.eclipse.chemclipse.chromatogram.alignment.model.core.RetentionIndices;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.NoRetentionIndexAvailableException;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.RetentionIndexExistsException;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.RetentionIndexValueException;
import junit.framework.TestCase;

/**
 * Testing RetentionIndex
 * 
 * @author eselmeister
 */
public class RetentionIndices_1_Test extends TestCase {

	private RetentionIndices retentionIndices;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		retentionIndices = new RetentionIndices();
	}

	@Override
	protected void tearDown() throws Exception {

		retentionIndices = null;
		super.tearDown();
	}

	public void testList_1() {

		try {
			retentionIndices.getActualRetentionIndex();
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("There is no retention index available", true);
		}
	}

	public void testList_2() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(5758, 1000.0f, "C41"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5760, 2000.0f, "C42"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5770, 3000.0f, "C43"));
			assertEquals("getActualRetentionIndex", "C43", retentionIndices.getActualRetentionIndex().getName());
			assertEquals("getPreviousRetentionIndex", "C42", retentionIndices.getPreviousRetentionIndex().getName());
			assertEquals("getPreviousRetentionIndex", "C41", retentionIndices.getPreviousRetentionIndex().getName());
			assertEquals("getNextRetentionIndex", "C42", retentionIndices.getNextRetentionIndex().getName());
			assertEquals("getNextRetentionIndex", "C43", retentionIndices.getNextRetentionIndex().getName());
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", false);
		}
	}

	public void testList_3() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(5758, 1000.0f, "C41"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5760, 2000.0f, "C42"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5770, 3000.0f, "C43"));
			assertEquals("getActualRetentionIndex", "C43", retentionIndices.getActualRetentionIndex().getName());
			assertEquals("getPreviousRetentionIndex", "C42", retentionIndices.getPreviousRetentionIndex().getName());
			assertEquals("getPreviousRetentionIndex", "C41", retentionIndices.getPreviousRetentionIndex().getName());
			retentionIndices.getPreviousRetentionIndex();
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", true);
		}
	}

	public void testList_4() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(5758, 1000.0f, "C41"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5760, 2000.0f, "C42"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5770, 3000.0f, "C43"));
			retentionIndices.getNextRetentionIndex().getName();
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", true);
		}
	}

	public void testList_5() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(5770, 3000.0f, "C43"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5758, 1000.0f, "C41"));
			retentionIndices.addRetentionIndex(new RetentionIndex(5760, 2000.0f, "C42"));
			assertEquals("getActualRetentionIndex", "C43", retentionIndices.getLastRetentionIndex().getName());
			assertEquals("getPreviousRetentionIndex", "C42", retentionIndices.getPreviousRetentionIndex().getName());
			assertEquals("getPreviousRetentionIndex", "C41", retentionIndices.getPreviousRetentionIndex().getName());
			assertEquals("getNextRetentionIndex", "C42", retentionIndices.getNextRetentionIndex().getName());
			assertEquals("getNextRetentionIndex", "C43", retentionIndices.getNextRetentionIndex().getName());
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", false);
		}
	}
}
