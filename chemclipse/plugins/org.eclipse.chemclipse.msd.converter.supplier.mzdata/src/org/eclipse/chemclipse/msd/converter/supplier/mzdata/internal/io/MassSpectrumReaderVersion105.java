/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
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
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.CvParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData.SpectrumList.Spectrum;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.ObjectFactory;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.ParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.PersonType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorMassSpectra;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorMassSpectra;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.VendorStandaloneMassSpectrum;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class MassSpectrumReaderVersion105 extends AbstractMassSpectraReader implements IMassSpectraReader {

	public static final String VERSION = "1.05";
	//
	private static final Logger logger = Logger.getLogger(MassSpectrumReaderVersion105.class);

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		IVendorStandaloneMassSpectrum massSpectrum = null;
		//
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName(ReaderVersion105.NODE_MZ_DATA);
			//
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			MzData mzData = (MzData)unmarshaller.unmarshal(nodeList.item(0));
			//
			massSpectrum = new VendorStandaloneMassSpectrum();
			massSpectrum.setSampleName(mzData.getDescription().getAdmin().getSampleName());
			for(PersonType contact : mzData.getDescription().getAdmin().getContact()) {
				String contactDetails = "";
				if(contact.getContactInfo() != null) {
					contactDetails = String.join(", ", contact.getName(), contact.getInstitution(), contact.getContactInfo());
				} else {
					contactDetails = String.join(", ", contact.getName(), contact.getInstitution());
				}
				if(massSpectrum.getOperator() == null || massSpectrum.getOperator().isEmpty()) {
					massSpectrum.setOperator(contactDetails);
				} else {
					massSpectrum.setOperator(String.join(", ", massSpectrum.getOperator(), contactDetails));
				}
			}
			massSpectrum.setInstrument(mzData.getDescription().getInstrument().getInstrumentName());
			massSpectrum.setFile(file);
			massSpectrum.setIdentifier(file.getName());
			ParamType processingMethod = mzData.getDescription().getDataProcessing().getProcessingMethod();
			if(processingMethod != null) {
				for(Object object : processingMethod.getCvParamOrUserParam()) {
					if(object instanceof CvParamType cvParamType) {
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
				IVendorIon ion = new VendorIon(mz, intensity);
				massSpectrum.addIon(ion);
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
