/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.assets.core;

import java.io.File;

import junit.framework.TestCase;

public class AssetItem_1_Test extends TestCase {

	public void test1() {

		AssetItem assetItem = new AssetItem(new File(""), AssetType.CONFIGURATION);
		assertNotNull(assetItem);
		assertNotNull(assetItem.getFile());
		assertEquals(AssetType.CONFIGURATION, assetItem.getAssetType());
	}
}