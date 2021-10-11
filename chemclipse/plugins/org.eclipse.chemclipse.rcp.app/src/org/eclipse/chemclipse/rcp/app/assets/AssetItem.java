/*******************************************************************************
 * Copyright (c) 2020, 2021 Christoph Läubrich.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.assets;

import java.io.File;

public class AssetItem {

	private File file;
	private AssetType assetType;

	public AssetItem(File file, AssetType assetType) {

		this.file = file;
		this.assetType = assetType;
	}

	public File getFile() {

		return file;
	}

	public AssetType getAssetType() {

		return assetType;
	}

	/**
	 * Convenient method to get the name.
	 * 
	 * @return {String}
	 */
	public String getName() {

		return file.getName();
	}

	/**
	 * Convenient method to get the description.
	 * 
	 * @return {String}
	 */
	public String getDescription() {

		return assetType.description();
	}
}
