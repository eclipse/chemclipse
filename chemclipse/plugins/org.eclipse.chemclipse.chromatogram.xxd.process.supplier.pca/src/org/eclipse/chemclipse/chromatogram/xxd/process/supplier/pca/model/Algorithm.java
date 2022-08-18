/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public enum Algorithm {

	SVD("SVD"), //
	NIPALS("NIPALS"), //
	OPLS("OPLS");

	private String name = "";

	private Algorithm(String name) {

		this.name = name;
	}

	public String getName() {

		return name;
	}

	public static Algorithm[] getAlgorithms() {

		return new Algorithm[]{Algorithm.SVD, Algorithm.NIPALS, Algorithm.OPLS};
	}
}