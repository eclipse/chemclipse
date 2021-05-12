/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

public class Tuple {

	private int position;
	private int index;

	public Tuple(int position, int index) {

		this.position = position;
		this.index = index;
	}

	public int getPosition() {

		return position;
	}

	public int getIndex() {

		return index;
	}
}