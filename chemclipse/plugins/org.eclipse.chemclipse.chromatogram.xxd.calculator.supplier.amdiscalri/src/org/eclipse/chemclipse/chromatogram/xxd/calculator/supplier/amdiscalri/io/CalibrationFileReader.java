/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.io.AMDISConverter;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;

public class CalibrationFileReader {

	public ISeparationColumnIndices parse(File file) {

		return new AMDISConverter().parse(file);
	}
}
