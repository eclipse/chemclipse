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
public class RetentionIndices_2_Test extends TestCase {

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

	public void testPreviousNextByTime_1() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(4500, 1000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(254500, 2000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(505500, 3000.0f));
			assertEquals("getPreviousRetentionIndex", 4500, retentionIndices.getPreviousRetentionIndex(5500).getRetentionTime());
			assertEquals("getNextRetentionIndex", 254500, retentionIndices.getNextRetentionIndex(5500).getRetentionTime());
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", false);
		}
	}

	public void testPreviousNextByTime_2() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(4500, 1000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(254500, 2000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(505500, 3000.0f));
			assertEquals("getPreviousRetentionIndex", 254500, retentionIndices.getPreviousRetentionIndex(255500).getRetentionTime());
			assertEquals("getNextRetentionIndex", 505500, retentionIndices.getNextRetentionIndex(255500).getRetentionTime());
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", false);
		}
	}

	public void testPreviousNextByIndex_1() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(4500, 1000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(254500, 2000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(505500, 3000.0f));
			assertEquals("getPreviousRetentionIndex", 4500, retentionIndices.getPreviousRetentionIndex(1100.0f).getRetentionTime());
			assertEquals("getNextRetentionIndex", 254500, retentionIndices.getNextRetentionIndex(1100.0f).getRetentionTime());
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", false);
		}
	}

	public void testPreviousNextByIndex_2() {

		try {
			retentionIndices.addRetentionIndex(new RetentionIndex(4500, 1000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(254500, 2000.0f));
			retentionIndices.addRetentionIndex(new RetentionIndex(505500, 3000.0f));
			assertEquals("getPreviousRetentionIndex", 254500, retentionIndices.getPreviousRetentionIndex(2100.0f).getRetentionTime());
			assertEquals("getNextRetentionIndex", 505500, retentionIndices.getNextRetentionIndex(2100.0f).getRetentionTime());
			assertEquals("getFirstRetentionIndex", 4500, retentionIndices.getFirstRetentionIndex().getRetentionTime());
		} catch(RetentionIndexExistsException e) {
			assertTrue("This state should never be entered. - RetentionIndexExistsException", false);
		} catch(RetentionIndexValueException e) {
			assertTrue("This state should never be entered. - RetentionIndexValueException", false);
		} catch(NoRetentionIndexAvailableException e) {
			assertTrue("This state should never be entered. - NoRetentionIndexAvailableException", false);
		}
	}
}
