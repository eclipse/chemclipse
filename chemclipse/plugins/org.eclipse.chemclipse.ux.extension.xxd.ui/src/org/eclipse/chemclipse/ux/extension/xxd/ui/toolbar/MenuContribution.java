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

import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

public class MenuContribution {

	private MMenuElement menuElement = null;
	private int index = -1;

	public MenuContribution(MMenuElement menuElement) {

		this(menuElement, -1);
	}

	public MenuContribution(MMenuElement menuElement, int index) {

		this.menuElement = menuElement;
		this.index = index;
	}

	public MMenuElement getMenuElement() {

		return menuElement;
	}

	public int getIndex() {

		return index;
	}
}
