/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISingleChromatogramReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.SingleChromatogramReport;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReportWriter implements IChromatogramReportWriter {

	@Override
	public void write(File file, ISingleChromatogramReport chromatogramReport, IProgressMonitor monitor) throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{SingleChromatogramReport.class});
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(chromatogramReport, file);
	}
}
