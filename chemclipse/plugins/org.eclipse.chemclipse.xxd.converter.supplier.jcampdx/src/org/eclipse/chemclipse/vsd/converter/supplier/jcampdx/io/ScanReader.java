/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - refactoring vibrational spectroscopy
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.math3.util.Precision;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.vsd.converter.supplier.jcampdx.model.IVendorSpectrumVSD;
import org.eclipse.chemclipse.vsd.converter.supplier.jcampdx.model.VendorSpectrumVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.core.runtime.IProgressMonitor;

import jakarta.activation.UnsupportedDataTypeException;

public class ScanReader {

	private static final Logger logger = Logger.getLogger(ScanReader.class);
	//
	private static final String COMMENT_MARKER = "$$";
	private static final String HEADER_MARKER = "##";
	private static final String DATE = "##DATE=";
	private static final String XUNITS = "##XUNITS=";
	private static final String YUNITS = "##YUNITS=";
	// private static final String RESOLUTION = "##RESOLUTION=";
	private static final String FIRSTX = "##FIRSTX=";
	private static final String LASTX = "##LASTX=";
	private static final String DELTAX = "##DELTAX=";
	// private static final String MAXY = "##MAXY=";
	// private static final String MINY = "##MINY=";
	private static final String XFACTOR = "##XFACTOR=";
	private static final String YFACTOR = "##YFACTOR=";
	private static final String NPOINTS = "##NPOINTS=";
	private static final String FIRSTY = "##FIRSTY=";
	private static final String XYDATA = "##XYDATA=";

	public IVendorSpectrumVSD read(File file, IProgressMonitor monitor) throws IOException {

		IVendorSpectrumVSD vendorScan = new VendorSpectrumVSD();
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		float firstX = 0;
		float firstY = 0;
		float lastX = 0;
		float deltaX = 0;
		double xFactor = 0;
		double yFactor = 0;
		float rawX = 0;
		int nPoints = 0;
		boolean firstValue = true;
		boolean transmission = false;
		boolean absorbance = false;
		while((line = bufferedReader.readLine()) != null) {
			if(line.contains(COMMENT_MARKER)) {
				line = line.substring(0, line.indexOf(COMMENT_MARKER));
			}
			if(line.startsWith(DATE)) {
				String date = line.trim().replace(DATE, "");
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				try {
					Date parsedDate = format.parse(date);
					vendorScan.setDate(parsedDate);
				} catch(ParseException e) {
					logger.warn(e);
				}
			}
			if(line.startsWith(NPOINTS)) {
				nPoints = Integer.parseInt(line.replace(NPOINTS, "").trim());
			}
			if(line.startsWith(XYDATA)) {
				if(!line.contains("(X++(Y..Y))")) {
					bufferedReader.close();
					fileReader.close();
					throw new UnsupportedDataTypeException("Unknown line compression type: " + line);
				}
			}
			if(line.startsWith(FIRSTX)) {
				firstX = Float.parseFloat(line.replace(FIRSTX, "").trim());
			}
			if(line.startsWith(FIRSTY)) {
				firstY = Float.parseFloat(line.replace(FIRSTY, "").trim());
			}
			if(line.startsWith(LASTX)) {
				lastX = Float.parseFloat(line.replace(LASTX, "").trim());
			}
			if(line.startsWith(DELTAX)) {
				deltaX = Float.parseFloat(line.replace(DELTAX, "").trim());
			}
			if(line.startsWith(XFACTOR)) {
				xFactor = Double.valueOf(line.replace(XFACTOR, "").trim());
			}
			if(line.startsWith(YFACTOR)) {
				yFactor = Double.valueOf(line.replace(YFACTOR, "").trim());
			}
			if(line.startsWith(XUNITS)) {
				String xUnit = line.replace(XUNITS, "").trim();
				if(!(xUnit.equals("1/CM") || xUnit.equals("WAVENUMBERS"))) {
					bufferedReader.close();
					fileReader.close();
					throw new UnsupportedDataTypeException("Unsupported X unit: " + xUnit);
				}
			}
			if(line.startsWith(YUNITS)) {
				String yUnit = line.replace(YUNITS, "").trim();
				if(yUnit.equals("TRANSMITTANCE")) {
					transmission = true;
				} else if(yUnit.equals("ABSORBANCE")) {
					absorbance = true;
				} else {
					bufferedReader.close();
					fileReader.close();
					throw new UnsupportedDataTypeException("Unsupported Y unit: " + yUnit);
				}
			}
			if(!line.startsWith(HEADER_MARKER)) {
				if(deltaX == 0) {
					if(firstX > lastX) {
						deltaX = -1;
					} else {
						deltaX = +1;
					}
				}
				try (Scanner scanner = new Scanner(line).useDelimiter("[^\\d]+")) {
					if(!scanner.hasNextInt()) {
						continue;
					}
					rawX = scanner.nextInt() - deltaX;
					while(scanner.hasNextInt()) {
						int rawY = scanner.nextInt();
						rawX += deltaX;
						double wavenumber = rawX * xFactor;
						double y = rawY * yFactor;
						if(firstValue) {
							if(!Precision.equalsWithRelativeTolerance(firstY, y, 0.1)) {
								logger.warn("Expected first Y to be " + firstY + " but calculated " + y);
							}
						}
						if(absorbance) {
							vendorScan.getScanVSD().getProcessedSignals().add(new SignalInfrared(wavenumber, y, 0));
						} else if(transmission) {
							vendorScan.getScanVSD().getProcessedSignals().add(new SignalInfrared(wavenumber, 0, y));
						}
						firstValue = false;
					}
				}
			}
		}
		if(nPoints > 0) {
			int signals = vendorScan.getScanVSD().getProcessedSignals().size();
			if(signals != nPoints) {
				logger.warn("Expected " + nPoints + " but got " + signals + " signals instead.");
			}
		}
		if(!Precision.equalsWithRelativeTolerance(lastX, rawX * xFactor, 0.1)) {
			logger.warn("Expected last X to be " + lastX + " but calculated " + rawX * xFactor);
		}
		bufferedReader.close();
		fileReader.close();
		return vendorScan;
	}
}
