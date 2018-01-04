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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.io;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISingleChromatogramReport;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramReportReader {

	ISingleChromatogramReport read(File file, IProgressMonitor monitor) throws JAXBException;
}
