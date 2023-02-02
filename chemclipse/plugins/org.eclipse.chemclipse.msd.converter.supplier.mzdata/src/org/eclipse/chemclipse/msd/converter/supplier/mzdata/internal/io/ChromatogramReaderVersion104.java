/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.AdminType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.CvParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.DataProcessingType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.DataProcessingType.Software;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.MzData;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.MzData.SpectrumList.Spectrum;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.ObjectFactory;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model.PersonType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.io.AbstractChromatogramReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class ChromatogramReaderVersion104 extends AbstractChromatogramReader implements IChromatogramMSDReader {

	public static final String VERSION = "1.04";
	//
	private static final Logger logger = Logger.getLogger(ChromatogramReaderVersion104.class);

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
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
			chromatogram = new VendorChromatogram();
			//
			AdminType admin = mzData.getDescription().getAdmin();
			chromatogram.setDataName(admin.getSampleName());
			for(PersonType contact : admin.getContact()) {
				String contactDetails = String.join(", ", contact.getName(), contact.getInstitution(), contact.getContactInfo());
				if(chromatogram.getOperator().isEmpty()) {
					chromatogram.setOperator(contactDetails);
				} else {
					chromatogram.setOperator(String.join(", ", chromatogram.getOperator(), contactDetails));
				}
			}
			chromatogram.setInstrument(mzData.getDescription().getInstrument().getInstrumentName());
			DataProcessingType dataProcessing = mzData.getDescription().getDataProcessing();
			Software software = dataProcessing.getSoftware();
			for(Object object : dataProcessing.getProcessingMethod().getCvParamOrUserParam()) {
				if(object instanceof CvParamType cvParamType) {
					chromatogram.getEditHistory().add(new EditInformation(cvParamType.getName(), software.getName() + " " + software.getVersion()));
				}
			}
			for(Spectrum spectrum : mzData.getSpectrumList().getSpectrum()) {
				/*
				 * Get the mass spectra.
				 */
				IVendorScan massSpectrum = new VendorScan();
				int retentionTime = 0;
				for(Object object : spectrum.getAcqDesc().getAcqSettings().getAcqInstrument().getCvParamOrUserParam()) {
					if(object instanceof CvParamType cvParamType) {
						if(cvParamType.getName().equals("time.min")) {
							retentionTime = Math.round(Float.parseFloat(cvParamType.getValue()) * 1000); // miliseconds
						}
					}
				}
				massSpectrum.setRetentionTime(retentionTime);
				/*
				 * Get the ions.
				 */
				double[] mz = ReaderVersion104.parseData(spectrum.getMzArrayBinary().getData());
				double[] intensities = ReaderVersion104.parseData(spectrum.getIntenArrayBinary().getData());
				int length = Math.min(mz.length, intensities.length);
				for(int index = 0; index < length; index++) {
					float intensity = (float)intensities[index];
					try {
						if(intensity >= VendorIon.MIN_ABUNDANCE && intensity <= VendorIon.MAX_ABUNDANCE) {
							IVendorIon ion = new VendorIon(mz[index], intensity);
							massSpectrum.addIon(ion);
						}
					} catch(AbundanceLimitExceededException e) {
						logger.warn(e);
					} catch(IonLimitExceededException e) {
						logger.warn(e);
					}
				}
				chromatogram.addScan(massSpectrum);
			}
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		}
		//
		chromatogram.setConverterId("");
		chromatogram.setFile(file);
		return chromatogram;
	}
}
