/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
package org.eclipse.chemclipse.model.targets;

public enum TargetReferenceType {
	NONE("None"), //
	SCAN("Scan"), //
	PEAK("Peak");

	private String label = "";

	private TargetReferenceType(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}
}
