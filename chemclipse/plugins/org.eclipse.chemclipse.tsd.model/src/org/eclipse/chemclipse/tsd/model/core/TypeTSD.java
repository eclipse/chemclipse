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
package org.eclipse.chemclipse.tsd.model.core;

import org.eclipse.chemclipse.support.text.ILabel;

public enum TypeTSD implements ILabel {

	GC_IMS("GC-IMS"), //
	HPLC_DAD("HPLC-DAD"), //
	GC_MS("GC-MS"), //
	HPLC_RAMAN("HPLC-Raman"), //
	GCxGC_FID("GCxGC-FID"), //
	GCxGC_MS("GCxGC-MS"); //

	private String label;

	private TypeTSD(String label) {

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