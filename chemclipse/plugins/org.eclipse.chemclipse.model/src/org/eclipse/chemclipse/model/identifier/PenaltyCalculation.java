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
package org.eclipse.chemclipse.model.identifier;

public enum PenaltyCalculation {
	NONE("None"), //
	RETENTION_TIME("Retention Time"), //
	RETENTION_INDEX("Retention Index"), //
	BOTH("Retention Time & Index"); //

	private String label = "";

	private PenaltyCalculation(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}
}
