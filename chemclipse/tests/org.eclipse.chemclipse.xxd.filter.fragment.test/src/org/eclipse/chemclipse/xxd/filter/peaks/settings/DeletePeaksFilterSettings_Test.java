/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import org.eclipse.chemclipse.xxd.filter.support.DeletePeakOption;

import junit.framework.TestCase;

public class DeletePeaksFilterSettings_Test extends TestCase {

	private DeletePeaksFilterSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new DeletePeaksFilterSettings();
	}

	public void test0() {

		settings.transferToLatestVersion("{\"Delete Peaks\":true}");
		assertEquals(DeletePeakOption.ALL, settings.getDeletePeakOption());
	}

	public void test1() {

		assertEquals(DeletePeakOption.NONE, settings.getDeletePeakOption());
	}

	public void test2() {

		settings.transferToLatestVersion(null);
		assertEquals(DeletePeakOption.NONE, settings.getDeletePeakOption());
	}

	public void test3() {

		settings.transferToLatestVersion("");
		assertEquals(DeletePeakOption.NONE, settings.getDeletePeakOption());
	}

	public void test4() {

		settings.transferToLatestVersion(" ");
		assertEquals(DeletePeakOption.NONE, settings.getDeletePeakOption());
	}

	public void test5() {

		settings.setDeletePeakOption(DeletePeakOption.INACTIVE);
		settings.transferToLatestVersion(null);
		assertEquals(DeletePeakOption.INACTIVE, settings.getDeletePeakOption());
	}

	public void test6() {

		settings.setDeletePeakOption(DeletePeakOption.INACTIVE);
		settings.transferToLatestVersion("");
		assertEquals(DeletePeakOption.INACTIVE, settings.getDeletePeakOption());
	}

	public void test7() {

		settings.setDeletePeakOption(DeletePeakOption.INACTIVE);
		settings.transferToLatestVersion(" ");
		assertEquals(DeletePeakOption.INACTIVE, settings.getDeletePeakOption());
	}

	public void test8() {

		settings.setDeletePeakOption(DeletePeakOption.INACTIVE);
		settings.transferToLatestVersion("{\"Delete Peakz\":false}"); // Can't match!
		assertEquals(DeletePeakOption.INACTIVE, settings.getDeletePeakOption());
	}
}