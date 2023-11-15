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
package org.eclipse.chemclipse.tsd.converter.chromatogram.model;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.tsd.model.core.TypeTSD;

public class ChromatogramAdapterMSD extends AbstractChromatogramAdapterTSD {

	private static final long serialVersionUID = 3706654786244251154L;

	public ChromatogramAdapterMSD(IChromatogram<?> chromatogram) {

		super(chromatogram);
	}

	@Override
	public String getName() {

		return extractNameFromFile("GC-MS");
	}

	@Override
	public String getLabelAxisX() {

		return "Ion [m/z]";
	}

	@Override
	public String getLabelAxisY() {

		return "Retention Time [min]";
	}

	@Override
	public TypeTSD getTypeTSD() {

		return TypeTSD.GC_MS;
	}
}