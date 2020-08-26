/*******************************************************************************
 * Copyright (c) 2020 Christoph Läubrich.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.cli;

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MsdReaderCommandLineProcessor extends ChromatogramReaderCommandLineProcessor {

	@Override
	protected IProcessingInfo<? extends IChromatogram<?>> load(File file, IProgressMonitor monitor) {

		return ChromatogramConverterMSD.getInstance().convert(file, monitor);
	}
}
