/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

public enum DataExplorerTreeRoot {
	NONE(""), //
	DRIVES("Drives"), //
	HOME("Home"), //
	USER_LOCATION("User Location");

	private String label;

	private DataExplorerTreeRoot(String label) {

		this.label = label;
	}

	@Override
	public String toString() {

		return this != NONE ? label : super.toString();
	}
}
