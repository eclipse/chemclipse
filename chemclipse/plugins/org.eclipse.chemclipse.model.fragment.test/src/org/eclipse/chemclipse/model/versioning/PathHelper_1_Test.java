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
package org.eclipse.chemclipse.model.versioning;

import java.io.File;

import org.eclipse.chemclipse.model.versioning.PathHelper;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

import junit.framework.TestCase;

public class PathHelper_1_Test extends TestCase {

	private String storagePath;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		storagePath = ApplicationSettings.getSettingsDirectory() + File.separator + PathHelper.CHROMATOGRAM_MODELS + File.separator + PathHelper.SERIALIZED_CHROMATOGRAMS;
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetStoragePath() {

		assertEquals("getStoragePath", storagePath, PathHelper.getStoragePath().getAbsolutePath());
	}
}
