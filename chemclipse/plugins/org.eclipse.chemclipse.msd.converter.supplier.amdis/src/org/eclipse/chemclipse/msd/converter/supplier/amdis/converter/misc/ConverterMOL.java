/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.cas.CasSupport;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class ConverterMOL {

	private static final Logger logger = Logger.getLogger(ConverterMOL.class);
	//
	private static final String MARKER_END = "M END";
	private static final Pattern patternCAS = Pattern.compile("(CAS)(\\s+)(rn)(\\s+)(=)(\\s+)((\\d+)(-?)(\\d+)(-?)(\\d+))");

	public static Map<String, String> convert(File file) {

		Map<String, String> moleculeStructureMap = new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			/*
			 * Header
			 */
			String line = null;
			String name = null;
			StringBuilder builder = new StringBuilder();
			//
			while((line = bufferedReader.readLine()) != null) {
				if(line.trim().isEmpty()) {
					continue;
				} else if(line.trim().equals(MARKER_END)) {
					if(name != null) {
						moleculeStructureMap.put(name.toUpperCase(), builder.toString().trim());
					}
					name = null;
					builder = new StringBuilder();
				} else {
					if(name == null) {
						name = line.trim();
					}
					builder.append(line);
					builder.append("\n");
				}
			}
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return moleculeStructureMap;
	}

	/**
	 * Converts e.g. the file
	 * ---
	 * /home/.../library.msl
	 * to
	 * /home/.../library.mol
	 * ---
	 * If the file doesn't exist, null is returned.
	 * 
	 * @param file
	 * @return File
	 */
	public static File getFileMOL(File file) {

		File fileMOL = null;
		if(file.isFile()) {
			String path = file.getParentFile().getAbsolutePath();
			String fileBaseName = FilenameUtils.getBaseName(file.getName());
			//
			fileMOL = new File(path + File.separator + fileBaseName + ".MOL");
			if(!fileMOL.exists()) {
				fileMOL = new File(path + File.separator + fileBaseName + ".mol");
				if(!fileMOL.exists()) {
					fileMOL = null;
				}
			}
		}
		//
		return fileMOL;
	}

	public static void transfer(Map<String, String> moleculeStructureMap, IMassSpectra massSpectra) {

		Map<String, String> casNumberMap = new HashMap<>();
		for(String moleculeStructure : moleculeStructureMap.values()) {
			String casNumber = extractCASNumber(moleculeStructure);
			if(!casNumber.isEmpty()) {
				casNumberMap.put(CasSupport.format(casNumber), moleculeStructure);
			}
		}
		//
		for(IScanMSD massSpectrum : massSpectra.getList()) {
			if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
				IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
				ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
				/*
				 * Get the molecule structure.
				 */
				String moleculeStructure = moleculeStructureMap.get(libraryInformation.getName().toUpperCase());
				if(moleculeStructure == null) {
					/*
					 * Use the CAS# instead.
					 */
					moleculeStructure = casNumberMap.get(CasSupport.format(libraryInformation.getCasNumber()));
				}
				/*
				 * Set if not null or empty.
				 */
				if(moleculeStructure != null && !moleculeStructure.isEmpty()) {
					libraryInformation.setMoleculeStructure(moleculeStructure);
				}
			}
		}
	}

	/**
	 * Returns the CAS# or "" if none is available.
	 * ... CAS rn = 19906720, ...
	 * ... CAS rn = 19906-72-0, ...
	 * 
	 * @return String
	 */
	public static String extractCASNumber(String moleculeStructure) {

		if(moleculeStructure != null && !moleculeStructure.isEmpty()) {
			Matcher matcher = patternCAS.matcher(moleculeStructure);
			if(matcher.find()) {
				return matcher.group(7);
			}
		}
		//
		return "";
	}
}