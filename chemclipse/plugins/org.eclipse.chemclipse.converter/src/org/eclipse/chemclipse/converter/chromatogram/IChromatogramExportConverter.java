/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.converter.core.IExportConverter;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramExportConverter extends IExportConverter {

	/**
	 * All implementations of this interface return a File object of the written
	 * chromatogram.<br/>
	 * File defines, where the chromatogram file will be stored.<br/>
	 * The class org.eclipse.chemclipse.msd.converter is responsible to parse
	 * a correct file.<br/>
	 * A correct file would be e.g. "../Chrom.D/Channel1A.DAD" for Agilent format. <br/>
	 * AbstractChromatogramExportConverter implements
	 * IChromatogramExportConverter. When extending from
	 * AbstractChromatogramExportConverter => super.validate(chromatogram) can
	 * be used.
	 * 
	 * @param file
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<File> convert(File file, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor);
}
