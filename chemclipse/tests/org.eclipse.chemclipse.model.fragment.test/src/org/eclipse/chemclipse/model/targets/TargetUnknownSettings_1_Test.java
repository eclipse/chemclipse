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
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.targets.TargetUnknownSettings;

import junit.framework.TestCase;

public class TargetUnknownSettings_1_Test extends TestCase {

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

		assertEquals("[", settings.getMarkerStart());
	}

	public void test2() {

		assertEquals("]", settings.getMarkerStop());
	}

	public void test3() {

		assertEquals(5, settings.getNumberTraces());
	}

	public void test4() {

		assertEquals("Unknown", settings.getTargetName());
	}

	public void test5() {

		assertEquals(false, settings.isIncludeIntensityPercent());
	}

	public void test6() {

		assertEquals(false, settings.isIncludeRetentionIndex());
	}

	public void test7() {

		assertEquals(false, settings.isIncludeRetentionTime());
	}
}
