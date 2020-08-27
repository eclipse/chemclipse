/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.assets;

import junit.framework.TestCase;

public class AssetType_1_Test extends TestCase {

	public void test1() {

		AssetType assetType = AssetType.CONFIGURATION;
		assertEquals("Configuration", assetType.getLabel());
		assertEquals(".cfg", assetType.getExtension());
		assertTrue(assetType.getDirectory().exists());
		assertEquals("Service Configuration File", assetType.getDescription());
	}

	public void test2() {

		AssetType assetType = AssetType.METHOD;
		assertEquals("Process Method", assetType.getLabel());
		assertEquals(".ocm", assetType.getExtension());
		assertTrue(assetType.getDirectory().exists());
		assertEquals("Process Method File", assetType.getDescription());
	}

	public void test3() {

		AssetType assetType = AssetType.PLUGIN;
		assertEquals("Plugin", assetType.getLabel());
		assertEquals(".jar", assetType.getExtension());
		assertTrue(assetType.getDirectory().exists());
		assertEquals("Plugin Extension", assetType.getDescription());
	}
}
