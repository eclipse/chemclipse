/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

	public SeparationColumn(String name, String length, String diameter, String phase) {
		setName(name);
		setLength(length);
		setDiameter(diameter);
		setPhase(phase);
	}
}
