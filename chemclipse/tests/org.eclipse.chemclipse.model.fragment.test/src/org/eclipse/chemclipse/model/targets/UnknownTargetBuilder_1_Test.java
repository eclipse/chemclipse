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
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class UnknownTargetBuilder_1_Test extends TestCase {

	public void test1() {

		TargetUnknownSettings settings = new TargetUnknownSettings();
		settings.setTargetName("Toluene");
		//
		IScan scan = new Scan(42);
		scan.setRetentionTime(5000);
		scan.setRetentionIndex(100);
		ILibraryInformation libraryInformation = UnknownTargetBuilder.getLibraryInformationUnknown(scan, settings, "");
		//
		assertEquals("Toluene []", libraryInformation.getName());
	}

	public void test2() {

		TargetUnknownSettings settings = new TargetUnknownSettings();
		settings.setIncludeIntensityPercent(false);
		settings.setIncludeRetentionIndex(false);
		settings.setIncludeRetentionTime(false);
		settings.setMarkerStart("");
		settings.setMarkerStop("");
		settings.setMatchQuality(85.0f);
		settings.setTargetName("Toluene");
		//
		IScan scan = new Scan(42);
		scan.setRetentionTime(5000);
		scan.setRetentionIndex(100);
		ILibraryInformation libraryInformation = UnknownTargetBuilder.getLibraryInformationUnknown(scan, settings, "");
		//
		assertEquals("Toluene", libraryInformation.getName());
	}
}