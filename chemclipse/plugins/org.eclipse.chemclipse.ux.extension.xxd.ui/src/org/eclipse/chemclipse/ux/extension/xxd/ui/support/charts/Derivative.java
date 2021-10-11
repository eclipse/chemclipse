/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import org.eclipse.chemclipse.support.text.ILabel;

public enum Derivative implements ILabel {

	NONE("--", 0), //
	FIRST("1st", 1), //
	SECOND("2nd", 2), //
	THIRD("3rd", 3);

	private String label = "";
	private int order = 0;

	private Derivative(String label, int order) {

		this.label = label;
		this.order = order;
	}

	@Override
	public String label() {

		return label;
	}

	public int order() {

		return order;
	}
}
