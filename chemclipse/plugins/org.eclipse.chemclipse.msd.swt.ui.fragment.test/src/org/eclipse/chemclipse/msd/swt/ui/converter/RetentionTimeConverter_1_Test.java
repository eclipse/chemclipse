/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.converter;

import org.eclipse.chemclipse.swt.ui.support.RetentionTimeConverter;

import junit.framework.TestCase;

public class RetentionTimeConverter_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testRetentionTime_1() {

		double minutes = RetentionTimeConverter.getRetentionTimeInMinutes(5600);
		assertEquals("Minutes", 0.09333333333333334d, minutes);
	}

	public void testRetentionTime_2() {

		double minutes = RetentionTimeConverter.getRetentionTimeInMinutes(5600.0d);
		assertEquals("Minutes", 0.09333333333333334d, minutes);
	}

	public void testRetentionTime_3() {

		double minutes = RetentionTimeConverter.getRetentionTimeInMinutes(0);
		assertEquals("Minutes", 0.0d, minutes);
	}

	public void testRetentionTime_4() {

		double minutes = RetentionTimeConverter.getRetentionTimeInMinutes(0.0d);
		assertEquals("Minutes", 0.0d, minutes);
	}

	public void testRetentionTime_5() {

		double minutes = RetentionTimeConverter.getRetentionTimeInMinutes(-5600);
		assertEquals("Minutes", -0.09333333333333334d, minutes);
	}

	public void testRetentionTime_6() {

		double minutes = RetentionTimeConverter.getRetentionTimeInMinutes(-5600.0d);
		assertEquals("Minutes", -0.09333333333333334d, minutes);
	}
}
