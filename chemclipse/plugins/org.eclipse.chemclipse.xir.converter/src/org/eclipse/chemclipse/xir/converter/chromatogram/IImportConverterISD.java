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
package org.eclipse.chemclipse.xir.converter.chromatogram;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IImportConverterISD extends IChromatogramImportConverter<IChromatogramISD> {

	IChromatogramISD convert(InputStream inputStream, IProgressMonitor monitor) throws IOException;

	IChromatogramOverview convertOverview(InputStream inputStream, IProgressMonitor monitor) throws IOException;
}