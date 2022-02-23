/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.support.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.CvParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData.SpectrumList.Spectrum;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.ParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorMassSpectra;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorMassSpectra;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MassSpectrumReaderVersion105 extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MassSpectrumReaderVersion105.class);
	private String contextPath;

	public MassSpectrumReaderVersion105(String contextPath) {

		this.contextPath = contextPath;
	}

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorStandaloneMassSpectrum massSpectrum = null;
		//
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName(IConstants.NODE_MZ_DATA);
			//
			JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			MzData mzData = (MzData)unmarshaller.unmarshal(nodeList.item(0));
			//
			massSpectrum = new VendorMassSpectrum();
			massSpectrum.setFile(file);
			massSpectrum.setIdentifier(file.getName());
			ParamType processingMethod = mzData.getDescription().getDataProcessing().getProcessingMethod();
			if(processingMethod != null) {
				for(Object object : processingMethod.getCvParamOrUserParam()) {
					if(object instanceof CvParamType) {
						CvParamType cvParamType = (CvParamType)object;
						if(cvParamType.getName().equals("peakProcessing")) {
							massSpectrum.setMassSpectrumType((short)(cvParamType.getValue().equals("centroided") ? 0 : 1));
						}
					}
				}
			}
			Spectrum spectrum = mzData.getSpectrumList().getSpectrum().get(0);
			double[] mzs = ReaderVersion105.parseData(spectrum.getMzArrayBinary().getData());
			double[] intensities = ReaderVersion105.parseData(spectrum.getIntenArrayBinary().getData());
			int length = Math.min(mzs.length, intensities.length);
			for(int i = 0; i < length; i++) {
				double mz = AbstractIon.getIon(mzs[i]);
				float intensity = (float)intensities[i];
				try {
					if(intensity >= VendorIon.MIN_ABUNDANCE && intensity <= VendorIon.MAX_ABUNDANCE) {
						IVendorIon ion = new VendorIon(mz, intensity);
						massSpectrum.addIon(ion);
					}
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		}
		//
		IVendorMassSpectra massSpectra = new VendorMassSpectra();
		massSpectra.setName(file.getName());
		massSpectra.addMassSpectrum(massSpectrum);
		return massSpectra;
	}
}