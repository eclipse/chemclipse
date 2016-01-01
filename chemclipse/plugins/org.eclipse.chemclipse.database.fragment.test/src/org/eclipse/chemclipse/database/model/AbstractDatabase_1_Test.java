/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import org.eclipse.chemclipse.support.settings.IOperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

import junit.framework.TestCase;

public class AbstractDatabase_1_Test extends TestCase {

	private IOperatingSystemUtils osUtils;
	private String database1 = "local:/home/user/.chemclipse/0.8.0/org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse/DefaultDB";
	private String database2 = "local:C:\\tmp\\.chemclipse\\0.8.0\\org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse\\DefaultDB";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		osUtils = new OperatingSystemUtils();
	}

	@Override
	protected void tearDown() throws Exception {

		osUtils = null;
		super.tearDown();
	}

	public void testGetOptimizedLocalDatabasePath_1() {

		String database1Expected = "local:$home$user$.chemclipse$0.8.0$org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse$DefaultDB";
		String database1Optimized = AbstractDatabase.getOptimizedLocalDatabasePath(database1);
		if(osUtils.isWindows()) {
			assertEquals(database1, database1Optimized);
		} else {
			assertEquals(database1Expected, database1Optimized);
		}
	}

	public void testGetOptimizedLocalDatabasePath_2() {

		String database2Expected = "local:C:$tmp$.chemclipse$0.8.0$org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse$DefaultDB";
		String database2Optimized = AbstractDatabase.getOptimizedLocalDatabasePath(database2);
		if(osUtils.isWindows()) {
			assertEquals(database2Expected, database2Optimized);
		} else {
			assertEquals(database2, database2Optimized);
		}
	}
}
