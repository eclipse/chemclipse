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

import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;

import junit.framework.TestCase;

/**
 * Tests the class BackgroundAbundanceRange.
 * 
 * @author eselmeister
 */
public class BackgroundAbundanceRange_1_Test extends TestCase {

	private IBackgroundAbundanceRange backgroundAbundanceRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		backgroundAbundanceRange = null;
		super.tearDown();
	}

	public void testSetup_1() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(0, 5);
		assertEquals("startBackgroundAbundance", 0.0f, backgroundAbundanceRange.getStartBackgroundAbundance());
		assertEquals("stopBackgroundAbundance", 5.0f, backgroundAbundanceRange.getStopBackgroundAbundance());
	}

	public void testSetup_2() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(5.0f, 0.0f);
		assertEquals("startBackgroundAbundance", 5.0f, backgroundAbundanceRange.getStartBackgroundAbundance());
		assertEquals("stopBackgroundAbundance", 0.0f, backgroundAbundanceRange.getStopBackgroundAbundance());
	}

	public void testSetup_3() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(-1, 5.0f);
		assertEquals("startBackgroundAbundance", IBackgroundAbundanceRange.MIN_BACKGROUND_ABUNDANCE, backgroundAbundanceRange.getStartBackgroundAbundance());
		assertEquals("stopBackgroundAbundance", 5.0f, backgroundAbundanceRange.getStopBackgroundAbundance());
	}

	public void testSetup_4() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(1, -1);
		assertEquals("startBackgroundAbundance", 1.0f, backgroundAbundanceRange.getStartBackgroundAbundance());
		assertEquals("stopBackgroundAbundance", IBackgroundAbundanceRange.MAX_BACKGROUND_ABUNDANCE, backgroundAbundanceRange.getStopBackgroundAbundance());
	}

	public void testSetup_5() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(IBackgroundAbundanceRange.MAX_BACKGROUND_ABUNDANCE + 1, 5.0f);
		// Because max abundance can't be exceeded.
		assertEquals("startBackgroundAbundance", IBackgroundAbundanceRange.MAX_BACKGROUND_ABUNDANCE, backgroundAbundanceRange.getStartBackgroundAbundance());
		assertEquals("stopBackgroundAbundance", 5.0f, backgroundAbundanceRange.getStopBackgroundAbundance());
	}

	public void testSetup_6() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(1, IBackgroundAbundanceRange.MAX_BACKGROUND_ABUNDANCE + 1);
		assertEquals("startBackgroundAbundance", 1.0f, backgroundAbundanceRange.getStartBackgroundAbundance());
		assertEquals("stopBackgroundAbundance", IBackgroundAbundanceRange.MAX_BACKGROUND_ABUNDANCE, backgroundAbundanceRange.getStopBackgroundAbundance());
	}
}
