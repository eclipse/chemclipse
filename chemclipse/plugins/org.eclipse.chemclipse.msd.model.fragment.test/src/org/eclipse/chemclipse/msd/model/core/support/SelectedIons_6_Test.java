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
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class SelectedIons_6_Test extends TestCase {

	private IMarkedIons selectedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		selectedIons = new MarkedIons();
		selectedIons.add(new MarkedIon(28.68301303f));
	}

	@Override
	protected void tearDown() throws Exception {

		selectedIons = null;
		super.tearDown();
	}

	public void testContains_1() {

		assertFalse("contains", selectedIons.contains(28.8f));
	}
	// public void testContains_1() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.8f, 1));
	// }
	//
	// public void testContains_2() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.79f, 2));
	// }
	//
	// public void testContains_3() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.787f, 3));
	// }
	//
	// public void testContains_4() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.7875f, 4));
	// }
	//
	// public void testContains_5() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.78749f, 5));
	// }
	//
	// public void testContains_6() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.787492f, 6));
	// }
	//
	// public void testContains_7() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.8f, 7));
	// }
	//
	// public void testContains_8() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.8f, 0));
	// }
	//
	// public void testContains_9() {
	//
	// assertFalse("contains", selectedIons.containsAccurate(28.8f, -1));
	// }
}
