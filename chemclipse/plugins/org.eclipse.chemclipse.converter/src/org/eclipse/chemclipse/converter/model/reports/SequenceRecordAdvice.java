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
package org.eclipse.chemclipse.converter.model.reports;

public enum SequenceRecordAdvice {
	NONE("No advice available yet."), //
	DATA_IS_VALID("The data is valid."), //
	FILE_NOT_AVAILABLE("The file is not available.");

	private String decsription;

	private SequenceRecordAdvice(String description) {
		this.decsription = description;
	}

	public String getDecsription() {

		return decsription;
	}
}
