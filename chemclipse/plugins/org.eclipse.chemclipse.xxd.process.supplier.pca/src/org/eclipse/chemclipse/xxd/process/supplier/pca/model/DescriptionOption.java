/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.support.text.ILabel;

public enum DescriptionOption implements ILabel {

	NONE("--"), //
	RETENTION_TIME_MIN("Retention Time (Minutes)"), //
	RETENTION_INDEX("Retention Index"), //
	NAME("Name"), //
	CAS("CAS#"), //
	NAME_CAS("Name (CAS#)"), //
	REFERENCE_IDENTIFIER("Reference Identifier"); //

	private String label;

	private DescriptionOption(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}