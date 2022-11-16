/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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

public enum LabelOptionPCA implements ILabel {

	SAMPLE_NAME("Sample Name"), //
	GROUP_NAME("Group Name"), //
	CLASSIFICATION("Classification"), //
	DESCRIPTION("Description"); //

	private String label = "";

	private LabelOptionPCA(String label) {

		this.label = label;
	}

	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}