/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

public class SeparationColumn extends AbstractSeparationColumn implements ISeparationColumn {

	private static final long serialVersionUID = -7621907201906074476L;

	public SeparationColumn(String name, SeparationColumnType separationColumnType) {

		this(name, separationColumnType, "", "", "");
	}

	public SeparationColumn(String name, SeparationColumnType separationColumnType, String length, String diameter, String phase) {

		setName(name);
		setSeparationColumnType(separationColumnType);
		setLength(length);
		setDiameter(diameter);
		setPhase(phase);
	}
}