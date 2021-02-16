/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import junit.framework.TestCase;

public class TargetUnknownSettings_2_Test extends TestCase {

	private TargetUnknownSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new TargetUnknownSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		settings.setMarkerStart(null);
		assertEquals("", settings.getMarkerStart());
		settings.setMarkerStart("");
		assertEquals("", settings.getMarkerStart());
		settings.setMarkerStart("(");
		assertEquals("(", settings.getMarkerStart());
	}

	public void test2() {

		settings.setMarkerStop(null);
		assertEquals("", settings.getMarkerStop());
		settings.setMarkerStop("");
		assertEquals("", settings.getMarkerStop());
		settings.setMarkerStop(")");
		assertEquals(")", settings.getMarkerStop());
	}

	public void test3() {

		settings.setNumberMZ(-1);
		assertEquals(0, settings.getNumberMZ());
		settings.setNumberMZ(0);
		assertEquals(0, settings.getNumberMZ());
		settings.setNumberMZ(1);
		assertEquals(1, settings.getNumberMZ());
	}

	public void test4() {

		settings.setTargetName(null);
		assertEquals("", settings.getTargetName());
		settings.setTargetName("");
		assertEquals("", settings.getTargetName());
		settings.setTargetName("unknown");
		assertEquals("unknown", settings.getTargetName());
	}

	public void test5() {

		settings.setIncludeIntensityPercent(true);
		assertEquals(true, settings.isIncludeIntensityPercent());
		settings.setIncludeIntensityPercent(false);
		assertEquals(false, settings.isIncludeIntensityPercent());
	}

	public void test6() {

		settings.setIncludeRetentionIndex(true);
		assertEquals(true, settings.isIncludeRetentionIndex());
		settings.setIncludeRetentionIndex(false);
		assertEquals(false, settings.isIncludeRetentionIndex());
	}

	public void test7() {

		settings.setIncludeRetentionTime(true);
		assertEquals(true, settings.isIncludeRetentionTime());
		settings.setIncludeRetentionTime(false);
		assertEquals(false, settings.isIncludeRetentionTime());
	}
}
