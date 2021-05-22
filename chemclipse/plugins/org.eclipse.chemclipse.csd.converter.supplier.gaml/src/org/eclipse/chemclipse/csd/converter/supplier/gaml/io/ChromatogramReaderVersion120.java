/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.gaml.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramReader;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.supplier.gaml.model.IVendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.gaml.model.VendorChromatogram;
import org.eclipse.chemclipse.csd.converter.supplier.gaml.model.VendorScan;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.support.PeakBuilderCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.io.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Experiment;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.GAML;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Parameter;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Peaktable;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Peaktable.Peak;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Peaktable.Peak.Baseline;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Technique;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Trace;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Units;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Xdata;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.internal.v120.model.Ydata;
import org.eclipse.chemclipse.xxd.converter.supplier.gaml.io.Reader120;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ChromatogramReaderVersion120 extends AbstractChromatogramReader implements IChromatogramCSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReaderVersion100.class);
	private String contextPath;

	public ChromatogramReaderVersion120(String contextPath) {

		this.contextPath = contextPath;
	}

	@Override
	public IChromatogramCSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		List<VendorChromatogram> chromatograms = getChromatograms(file);
		if(chromatograms.isEmpty())
			return null;
		VendorChromatogram chromatogram = chromatograms.get(0);
		chromatograms.stream().skip(1).forEach(chromatogram::addReferencedChromatogram);
		return chromatogram;
	}

	List<VendorChromatogram> getChromatograms(File file) {

		List<VendorChromatogram> chromatograms = new ArrayList<VendorChromatogram>();
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName(IConstants.NODE_GAML);
			JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			GAML gaml = (GAML)unmarshaller.unmarshal(nodeList.item(0));
			for(Experiment experiment : gaml.getExperiment()) {
				VendorChromatogram chromatogram = new VendorChromatogram();
				chromatogram.setDataName(experiment.getName());
				XMLGregorianCalendar collectDate = experiment.getCollectdate();
				if(collectDate != null)
					chromatogram.setDate(collectDate.toGregorianCalendar().getTime());
				chromatogram.setConverterId("");
				chromatogram.setFile(file);
				for(Parameter parameter : experiment.getParameter()) {
					if(parameter.getName().equals("limsID"))
						chromatogram.setBarcode(parameter.getValue());
				}
				for(Trace trace : experiment.getTrace()) {
					double[] retentionTimes = null;
					double[] intensities = null;
					if(trace.getTechnique() == Technique.CHROM) {
						for(Xdata xdata : trace.getXdata()) {
							Units unit = xdata.getUnits();
							retentionTimes = Reader120.parseValues(xdata.getValues());
							Ydata ydata = xdata.getYdata().get(0);
							intensities = Reader120.parseValues(ydata.getValues());
							int scans = Math.min(retentionTimes.length, intensities.length);
							for(int i = 0; i < scans; i++) {
								VendorScan scan = new VendorScan((float)intensities[i]);
								scan.setRetentionTime(Reader120.convertToMiliSeconds(retentionTimes[i], unit));
								chromatogram.addScan(scan);
							}
							for(Peaktable peaktable : ydata.getPeaktable()) {
								for(Peak peak : peaktable.getPeak()) {
									Baseline baseline = peak.getBaseline();
									int startScan = chromatogram.getScanNumber((float)baseline.getStartXvalue());
									int stopScan = chromatogram.getScanNumber((float)baseline.getEndXvalue());
									IScanRange scanRange = new ScanRange(startScan, stopScan);
									try {
										IChromatogramPeakCSD chromatogramPeak = PeakBuilderCSD.createPeak(chromatogram, scanRange, true);
										if(peak.getName() != null) {
											ILibraryInformation libraryInformation = new LibraryInformation();
											libraryInformation.setName(peak.getName());
											IComparisonResult comparisonResult = ComparisonResult.createBestMatchComparisonResult();
											IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
											chromatogramPeak.getTargets().add(identificationTarget);
										}
										chromatogram.addPeak(chromatogramPeak);
									} catch(Exception e) {
										logger.warn("Peak " + peak.getNumber() + " could not be added.");
									}
								}
							}
						}
					}
				}
				chromatograms.add(chromatogram);
			}
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
		return chromatograms;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorChromatogram chromatogram = null;
		//
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName(IConstants.NODE_GAML);
			//
			JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			GAML gaml = (GAML)unmarshaller.unmarshal(nodeList.item(0));
			//
			chromatogram = new VendorChromatogram();
			//
			Experiment experiment = gaml.getExperiment().get(0);
			chromatogram.setDataName(experiment.getName());
			chromatogram.setDate(experiment.getCollectdate().toGregorianCalendar().getTime());
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		}
		return chromatogram;
	}
}
