/*******************************************************************************
 * Copyright (c) 2016, 2021 Dr. Janko Diminic.
 * 
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ProteomsUtil;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.Peak;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMSMS;

/**
 *
 * Parser for MSF from "4000 Series Explorer" tool exported with command
 * "Peaks to Mascot". Instrument is 4800 MALDI TOF/TOF Analyzer, Applied
 * Biosystems, MDS Sciex, USA. Every MGF file contain 1 MS data set, with 0 or more
 * MSMS data.
 *
 * Alternative class org.eclipse.chemclipse.msd.converter.supplier.mgf.converter.ChromatogramImportConverter.
 *
 * @author Janko Diminic
 *
 *
 */
public class MGFParser {

	private static final Pattern labelPattern = Pattern.compile(".*Label:(.*), Spot Id:.*");

	public void parserRegular(File file) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		// MGFFileReaderImpl reader = new MGFFileReaderImpl(file);
		// MGFFile result = reader.read();
		// // one MS1 element, four MS2 elements
		// MSSpectrum parentMS;
		// List<MGFElement> elements = result.getElements();
		// for(MGFElement el : elements) {
		// short msLevel = el.getMSLevel();
		// if(msLevel == 2) {
		// parentMS = new MSSpectrum();
		// parentMS.setName(el.getTag("LABEL"));
		// List<net.sf.bioutils.proteomics.peak.Peak> peaks = el.getPeaks();
		// for(net.sf.bioutils.proteomics.peak.Peak peak : peaks) {
		// parentMS.addPeak(new Peak(peak.getMz(), peak.getIntensity()));
		// }
		// }
		// }
		// for(int i = 1; i < elements.size(); i++) {
		// assertEquals(2, elements.get(i).getMSLevel());
		// }
		// // all elements should have the 'COM' tag
		// for(int i = 0; i < elements.size(); i++) {
		// assertNotNull(elements.get(i).getTag("COM"));
		// }
		// // all MS2 elements should have a valid PEPMASS tag
		// for(int i = 0; i < elements.size(); i++) {
		// if(elements.get(i).getMSLevel() == 2) {
		// String o = elements.get(i).getTag("PEPMASS");
		// assertNotNull(o);
		// // should be a number
		// Double.parseDouble(o);
		// }
		// }
		// MGFReader r = new MGFReader();
		// IMassSpectra res = r.read(file, new NullProgressMonitor());
		// List<IScanMSD> list = res.getList();
		// log.debug("Size " + list.size());
		// for(IScanMSD s : list) {
		// IRegularMassSpectrum spectra = (IRegularMassSpectrum)s;
		// double precursorIon = spectra.getPrecursorIon();
		// short level = spectra.getMassSpectrumType();
		// log.debug("level " + level + " prec " + precursorIon);
		// List<IIon> ions = s.getIons();
		// int c = 0;
		// for(IIon i : ions) {
		// double mz = i.getIon();
		// float intensity = i.getAbundance();
		// log.debug("mz " + mz + " intensity " + intensity);
		// if(c++ == 5) {
		// break;
		// }
		// }
		// }
	}

	/**
	 * Parse one MGF file.
	 *
	 * @return {@link SpectrumMS}
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SpectrumMS parse(String mgfFilePath) throws FileNotFoundException, IOException {

		File f = new File(mgfFilePath);
		try (FileInputStream input = new FileInputStream(f)) {
			List<String> lines = IOUtils.readLines(input, Charset.forName("UTF-8"));
			SpectrumMS ms1 = parseMGFLines(lines);
			return ms1;
		}
	}

	/**
	 * 
	 * Return MS name from first line.
	 * 
	 * @param MGF
	 *            line
	 * @return MS name
	 */
	private String findInFirstLineMSname(String line) {

		final Matcher matcher = labelPattern.matcher(line);
		if(matcher.find()) {
			int groupCount = matcher.groupCount();
			if(groupCount == 1) {
				return matcher.group(1);
			}
		}
		throw new RuntimeException("Not find MS name in line: '" + line + "'");
	}

	private SpectrumMS parseMGFLines(final List<String> lines) {

		boolean firstLine = true;
		boolean startMSpeaks = false;
		boolean startMSMSHeader = false;
		boolean startMSMSPeaks = false;
		SpectrumMS ms = new SpectrumMS();
		// ms.setPositive(false);
		// ms.setMS(true);
		// ms1List.add(ms);
		// ms.setAllPeaks(new ArrayList<Peak>(175));
		SpectrumMSMS currentMSMS = null;
		for(String line : lines) {
			if(line == null || line.isEmpty()) {
				continue;
			}
			if(firstLine) { // 1
				final String msName = findInFirstLineMSname(line).trim();
				ms.setName(msName);
				ms.setId(ProteomsUtil.nextUniqueID());
				startMSpeaks = true;
				firstLine = false;
				continue;
			}
			if(startMSpeaks) { // 2
				if(line.contains(IMGFAbbreviations.BEGIN_IONS)) {
					// finish MS peaks
					startMSpeaks = false;
					startMSMSHeader = true;
					continue;
				}
				Peak p = getPeaks(line);
				ms.addPeak(p);
			}
			if(startMSMSHeader) {
				if(line.startsWith(IMGFAbbreviations.TITLE)) {
					startMSMSHeader = false;
					startMSMSPeaks = true;
					continue;
				}
				if(line.startsWith(IMGFAbbreviations.PEPMASS + "=")) {
					String massPrecursor = line.substring(8, line.length());
					currentMSMS = new SpectrumMSMS();
					currentMSMS.setId(ProteomsUtil.nextUniqueID());
					// currentMSMS.setAllPeaks(new ArrayList<Peak>(300));
					Peak precursorPeak = new Peak(Double.valueOf(massPrecursor.trim()), 0);
					// currentMSMS.setMassPrecursor(Double.valueOf(massPrecursor.trim()).floatValue());
					// currentMSMS.setDateCreated(new Date());
					// currentMSMS.setMS(false);
					currentMSMS.setParentMS(ms);
					currentMSMS.setPrecursorPeak(precursorPeak);
					// currentMSMS.setPositive(false);
					ms.addMSMSchild(currentMSMS);
					currentMSMS.setName(massPrecursor.trim());
					continue;
				}
			}
			if(startMSMSPeaks) {
				if(line.startsWith(IMGFAbbreviations.END_IONS)) {
					// finish ions
					startMSMSPeaks = false;
					startMSMSHeader = true;
					continue;
				}
				final Peak p = getPeaks(line);
				currentMSMS.addPeak(p);
			}
		}
		return ms;
	}

	private Peak getPeaks(String line) {

		final String[] split = line.split("\t");
		double mass = Double.parseDouble(split[0]);
		double intensity = Double.parseDouble(split[1]);
		return new Peak(mass, intensity);
	}
}
