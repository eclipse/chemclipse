/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.supplier.cml.io;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.xir.model.core.ISignalVS;
import org.eclipse.chemclipse.xir.model.core.ISpectrumXIR;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Array;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Cml;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Spectrum;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.SpectrumData;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.SpectrumType;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Xaxis;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Yaxis;
import org.eclipse.core.runtime.IProgressMonitor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class ScanWriter {

	private static final Logger logger = Logger.getLogger(ScanWriter.class);

	public void write(File file, ISpectrumXIR ir, IProgressMonitor monitor) {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Cml.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			Cml cml = new Cml();
			cml.setSpectrum(createSpectrum(ir));
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.xml-cml.org/schema ../../schema.xsd");
			marshaller.marshal(cml, file);
		} catch(JAXBException e) {
			logger.warn(e);
		}
	}

	private Spectrum createSpectrum(ISpectrumXIR ir) {

		Spectrum spectrum = new Spectrum();
		spectrum.setType(SpectrumType.INFRARED);
		spectrum.setSpectrumData(createSpectrumData(ir));
		return spectrum;
	}

	private SpectrumData createSpectrumData(ISpectrumXIR ir) {

		SpectrumData spectrumData = new SpectrumData();
		spectrumData.setXaxis(createXAxis(ir));
		spectrumData.setYaxis(createYAxis(ir));
		return spectrumData;
	}

	private Xaxis createXAxis(ISpectrumXIR ir) {

		Xaxis xAxis = new Xaxis();
		StringBuilder wavenumbers = new StringBuilder();
		for(ISignalVS signal : ir.getScanISD().getProcessedSignals()) {
			wavenumbers.append(signal.getWavenumber());
			wavenumbers.append(" ");
		}
		Array array = createArray();
		array.setValue(wavenumbers.toString());
		array.setUnits("newunits:cm-1");
		xAxis.setArray(array);
		return xAxis;
	}

	private Yaxis createYAxis(ISpectrumXIR ir) {

		Yaxis yAxis = new Yaxis();
		StringBuilder absorbances = new StringBuilder();
		for(ISignalVS signal : ir.getScanISD().getProcessedSignals()) {
			absorbances.append(signal.getIntensity());
			absorbances.append(" ");
		}
		Array array = createArray();
		array.setValue(absorbances.toString());
		array.setUnits("cml:absorbance");
		yAxis.setArray(array);
		return yAxis;
	}

	private Array createArray() {

		Array array = new Array();
		array.setDataType("xsd:Double");
		return array;
	}
}
