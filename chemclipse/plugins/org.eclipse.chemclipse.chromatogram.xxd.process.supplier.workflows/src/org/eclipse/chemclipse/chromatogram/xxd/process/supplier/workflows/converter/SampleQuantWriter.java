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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.core.runtime.IProgressMonitor;

public class SampleQuantWriter implements ISampleQuantWriter {

	@Override
	public void write(File file, ISampleQuantReport sampleQuantReport, IProgressMonitor monitor) throws Exception {

		SampleQuantWriter_1200 sampleQuantWriter = new SampleQuantWriter_1200();
		sampleQuantWriter.write(file, sampleQuantReport, monitor);
	}
}
