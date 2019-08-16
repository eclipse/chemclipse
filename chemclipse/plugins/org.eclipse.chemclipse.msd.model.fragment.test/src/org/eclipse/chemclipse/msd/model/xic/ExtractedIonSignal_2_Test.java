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
 * 20); extractedIonSignal2 = new ExtractedIonSignal(1, 20);
 * 
 * @author eselmeister
 */
public class ExtractedIonSignal_2_Test extends TestCase {

	private IExtractedIonSignal extractedIonSignal1;
	private IExtractedIonSignal extractedIonSignal2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		extractedIonSignal1 = new ExtractedIonSignal(1, 20);
		extractedIonSignal2 = new ExtractedIonSignal(1, 20);
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignal1 = null;
		extractedIonSignal2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertTrue("equals", extractedIonSignal1.equals(extractedIonSignal2));
	}

	public void testEquals_2() {

		assertTrue("equals", extractedIonSignal2.equals(extractedIonSignal1));
	}

	public void testHashCode_1() {

		assertTrue("hashCode", extractedIonSignal1.hashCode() == extractedIonSignal2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue("hashCode", extractedIonSignal2.hashCode() == extractedIonSignal1.hashCode());
	}
}
