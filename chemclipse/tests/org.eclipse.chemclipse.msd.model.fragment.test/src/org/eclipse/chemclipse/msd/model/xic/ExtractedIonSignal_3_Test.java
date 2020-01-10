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
package org.eclipse.chemclipse.msd.model.xic;

import junit.framework.TestCase;

/**
 * HashCode and equals test. extractedIonSignal1 = new ExtractedIonSignal(1,
 * 15); extractedIonSignal2 = new ExtractedIonSignal(1, 20);
 * 
 * @author eselmeister
 */
public class ExtractedIonSignal_3_Test extends TestCase {

	private IExtractedIonSignal extractedIonSignal1;
	private IExtractedIonSignal extractedIonSignal2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		extractedIonSignal1 = new ExtractedIonSignal(1, 15);
		extractedIonSignal2 = new ExtractedIonSignal(1, 20);
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignal1 = null;
		extractedIonSignal2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse("equals", extractedIonSignal1.equals(extractedIonSignal2));
	}

	public void testEquals_2() {

		assertFalse("equals", extractedIonSignal2.equals(extractedIonSignal1));
	}

	public void testEquals_3() {

		assertTrue("equals", extractedIonSignal1.equals(extractedIonSignal1));
	}

	public void testEquals_4() {

		assertTrue("equals", extractedIonSignal2.equals(extractedIonSignal2));
	}

	public void testEquals_5() {

		assertFalse("equals", extractedIonSignal1.equals(null));
	}

	public void testEquals_6() {

		assertFalse("equals", extractedIonSignal2.equals(null));
	}

	public void testEquals_7() {

		assertFalse("equals", extractedIonSignal1.equals(new String("Test")));
	}

	public void testEquals_8() {

		assertFalse("equals", extractedIonSignal2.equals(new String("Test")));
	}

	public void testHashCode_1() {

		assertTrue("hashCode", extractedIonSignal1.hashCode() != extractedIonSignal2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue("hashCode", extractedIonSignal2.hashCode() != extractedIonSignal1.hashCode());
	}
}
