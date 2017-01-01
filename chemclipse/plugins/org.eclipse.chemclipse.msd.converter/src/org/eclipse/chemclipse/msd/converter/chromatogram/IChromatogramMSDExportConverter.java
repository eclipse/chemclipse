/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramExportConverter;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

public interface IChromatogramMSDExportConverter extends IChromatogramExportConverter {

	/**
	 * All implementations of this interface return a File object of the written
	 * chromatogram.<br/>
	 * File defines, where the chromatogram file will be stored.<br/>
	 * The class org.eclipse.chemclipse.msd.converter is responsible to parse
	 * a correct file.<br/>
	 * A correct file would be e.g. "../Chrom.D/DATA.MS" for Agilent format and
	 * "../Chrom.ionxml" for IonXML format.<br/>
	 * <br/>
	 * AbstractChromatogramExportConverter implements
	 * IChromatogramExportConverter. When extending from
	 * AbstractChromatogramExportConverter => super.validate(chromatogram) can
	 * be used.
	 * 
	 * @param file
	 * @param chromatogram
	 * @param monitor
	 * @return {@link IChromatogramExportConverterProcessingInfo}
	 */
	IChromatogramExportConverterProcessingInfo convert(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor);
}
