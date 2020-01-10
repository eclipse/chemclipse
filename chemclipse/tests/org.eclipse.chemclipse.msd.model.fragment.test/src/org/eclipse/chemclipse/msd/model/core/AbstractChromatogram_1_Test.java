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
package org.eclipse.chemclipse.msd.model.core;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.versioning.PathHelper;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

import junit.framework.TestCase;

public class AbstractChromatogram_1_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private File storagePath;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testGetStorageDirectory_1() {

		/*
		 * Test the storage path cases.<br/>The time id could differ in the last
		 * digits so leave the last digits out of view.<br/><br/> test =
		 * /home/eselmeister
		 * /.chemclipse/org.eclipse.chemclipse.msd.model/serializedChromatograms
		 * /chromatogram@1b2e165_12246569375[02]<br/> actual =
		 * /home/eselmeister/
		 * .chemclipse/org.eclipse.chemclipse.msd.model/serializedChromatograms
		 * /chromatogram@1b2e165_12246569375[03]<br/>
		 */
		storagePath = new File(ApplicationSettings.getSettingsDirectory() + File.separator + PathHelper.CHROMATOGRAM_MODELS + File.separator + PathHelper.SERIALIZED_CHROMATOGRAMS + File.separator + chromatogram.getIdentifier());
		String test = storagePath.getAbsolutePath();
		String actual = chromatogram.getStorageDirectory().getAbsolutePath();
		assertTrue("storageDirectory: " + actual, test.regionMatches(0, actual, 0, actual.length() - 2));
	}

	public void testGetIdentifier_1() {

		/*
		 * This methods test the identifier e.g. against a pattern.<br/>
		 * chromatogram@934847_1224656529065
		 */
		// String regex = "(chromatogram@)[0-9]+(_)(0-9)+";
		String regex = "(chromatogram@)[0-9a-fA-F]+(_)[0-9]+$";
		Pattern pattern = Pattern.compile(regex);
		String identifier = chromatogram.getIdentifier();
		Matcher m = pattern.matcher(identifier);
		assertTrue("Identifier: " + identifier, m.find());
	}
}
