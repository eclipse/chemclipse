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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.ISettingStatus;

import junit.framework.TestCase;

public class SettingStatus_1_Test extends TestCase {

	private ISettingStatus settingStatus;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testStatus_1() {

		settingStatus = new SettingStatus(true, true);
		assertTrue("Report", settingStatus.report());
		assertTrue("SumOn", settingStatus.sumOn());
	}

	public void testStatus_2() {

		settingStatus = new SettingStatus(true, false);
		assertTrue("Report", settingStatus.report());
		assertFalse("SumOn", settingStatus.sumOn());
	}

	public void testStatus_3() {

		settingStatus = new SettingStatus(false, true);
		assertFalse("Report", settingStatus.report());
		assertTrue("SumOn", settingStatus.sumOn());
	}

	public void testStatus_4() {

		settingStatus = new SettingStatus(false, false);
		assertFalse("Report", settingStatus.report());
		assertFalse("SumOn", settingStatus.sumOn());
	}
}
