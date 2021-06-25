/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.core;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramTSD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IImportConverterTSD extends IChromatogramImportConverter<IChromatogramTSD> {

	IChromatogramTSD convert(InputStream inputStream, IProgressMonitor monitor) throws IOException;

	IChromatogramOverview convertOverview(InputStream inputStream, IProgressMonitor monitor) throws IOException;
}