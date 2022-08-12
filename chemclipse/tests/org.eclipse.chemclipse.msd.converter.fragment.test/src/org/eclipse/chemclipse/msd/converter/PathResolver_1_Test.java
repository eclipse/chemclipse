/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore("Runs fine in the IDE, fails via Maven.")
public class PathResolver_1_Test extends TestCase {

	public void testGetAbsolutePath_1() {

		try {
			String location = Activator.getContext().getBundle().getLocation();
			location = location.replace("reference:file:", "");
			if(location.endsWith("/")) {
				location = location.substring(0, location.length() - 1);
			}
			location = location.concat(".fragment.test");
			File file = new File(location);
			String path = file.getAbsolutePath() + "/testData/files/import/EMPTY.D/DATA.MS";
			String test = PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_EMPTY);
			// Handle Windows systems in a different way than unix/linux :-).
			String osName = System.getProperty("os.name").toLowerCase();
			if(osName.contains("windows")) {
				if(test.startsWith("/")) {
					test = test.substring(1);
				}
				test = test.replace('\\', '/');
				path = path.replace('\\', '/');
			}
			/*
			 * Tests folder, but path resolver is located in plugins.
			 * .../git/chemclipse/chemclipse/tests/org.eclipse.chemclipse.msd.converter.fragment.test/testData/files/import/EMPTY.D/DATA.MS
			 */
			path = path.replace("plugins", "tests");
			assertEquals(path, test);
		} catch(IOException e) {
			assertTrue("IOException", false);
		}
	}
}
