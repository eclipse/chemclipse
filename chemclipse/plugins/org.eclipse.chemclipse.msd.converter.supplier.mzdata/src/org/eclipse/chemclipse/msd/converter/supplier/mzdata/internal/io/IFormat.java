/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io;

public interface IFormat {

	String CONTEXT_PATH_V_104 = "org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model";
	String CONTEXT_PATH_V_105 = "org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model";
	String MZDATA_V_104 = "mzDATA_1.04";
	String MZDATA_V_105 = "mzDATA_1.05";
	String CHROMATOGRAM_VERSION_LATEST = MZDATA_V_105;
}
