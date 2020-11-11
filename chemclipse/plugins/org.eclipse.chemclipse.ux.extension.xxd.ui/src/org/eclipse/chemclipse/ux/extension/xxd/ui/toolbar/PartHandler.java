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

public class PartHandler extends AbstractPartHandler {

	private String name = "";
	private PartStackReference partStackReference;

	public PartHandler(String name, String partId, String stackPositionKey) {

		this.name = name;
		this.partStackReference = new PartStackReference(partId, stackPositionKey);
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public PartStackReference getPartStackReference() {

		return partStackReference;
	}
}
