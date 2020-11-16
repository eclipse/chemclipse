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

public enum Action {
	SHOW("Show"), //
	HIDE("Hide");

	private String label = "";

	private Action(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}

	public String getId() {

		return label.toLowerCase();
	}
}
