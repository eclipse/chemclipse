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
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

public class PartStackReference {

	private String partId = "";
	private String stackPositionKey = "";

	public PartStackReference(String partId, String stackPositionKey) {

		this.partId = partId;
		this.stackPositionKey = stackPositionKey;
	}

	public String getPartId() {

		return partId;
	}

	public String getStackPositionKey() {

		return stackPositionKey;
	}
}
