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
package org.eclipse.chemclipse.xir.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.xir.converter.supplier.jcampdx.model.IVendorScanXIR;
import org.eclipse.chemclipse.xir.converter.supplier.jcampdx.model.VendorScanXIR;
import org.eclipse.chemclipse.xir.model.core.SignalXIR;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanReader {

	private static final Logger logger = Logger.getLogger(ScanReader.class);
	//
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
	// private static final String NPOINTS = "##NPOINTS=";
	private static final String FIRSTY = "##FIRSTY=";
	private static final String XYDATA = "##XYDATA=";
	//
	private static final Pattern rawYpattern = Pattern.compile("\\+(\\d*)");

	public IVendorScanXIR read(File file, IProgressMonitor monitor) throws IOException {

		IVendorScanXIR vendorScan = new VendorScanXIR();
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
		boolean firstValue = true;
		boolean transmission = false;
		boolean absorbance = false;
		while((line = bufferedReader.readLine()) != null) {
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
			if(line.startsWith(XYDATA)) {
				if(!line.contains("(X++(Y..Y))")) {
					bufferedReader.close();
					fileReader.close();
					throw new UnsupportedDataTypeException("Unknown line compression type: " + line);
				}
			}
			if(line.startsWith(FIRSTX)) {
				firstX = Float.parseFloat(line.trim().replace(FIRSTX, ""));
				rawX = firstX;
			}
			if(line.startsWith(FIRSTY)) {
				firstY = Float.parseFloat(line.trim().replace(FIRSTY, ""));
			}
			if(line.startsWith(LASTX)) {
				lastX = Float.parseFloat(line.trim().replace(LASTX, ""));
			}
			if(line.startsWith(DELTAX)) {
				deltaX = Float.parseFloat(line.trim().replace(DELTAX, ""));
			}
			if(line.startsWith(XFACTOR)) {
				xFactor = Double.valueOf(line.trim().replace(XFACTOR, ""));
			}
			if(line.startsWith(YFACTOR)) {
				yFactor = Double.valueOf(line.trim().replace(YFACTOR, ""));
			}
			if(line.startsWith(XUNITS)) {
				String xUnit = line.trim().replace(XUNITS, "");
				if(!xUnit.equals("1/CM")) {
					bufferedReader.close();
					fileReader.close();
					throw new UnsupportedDataTypeException("Unsupported X unit: " + xUnit);
				}
			}
			if(line.startsWith(YUNITS)) {
				String yUnit = line.trim().replace(YUNITS, "");
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
				Matcher rawYs = rawYpattern.matcher(line.trim());
				while(rawYs.find()) {
					if(!firstValue) {
						rawX += deltaX;
					}
					double x = rawX * xFactor;
					//
					int rawY = Integer.parseInt(rawYs.group(1));
					double y = rawY * yFactor;
					if(firstValue) {
						if(firstY != y) { // TODO approximate here
							logger.warn("Expected first Y to be " + firstY + " but calculated " + y);
						}
					}
					if(absorbance) {
						vendorScan.getProcessedSignals().add(new SignalXIR(x, y, 0));
					} else if(transmission) {
						vendorScan.getProcessedSignals().add(new SignalXIR(x, 0, y));
					}
					firstValue = false;
				}
			}
		}
		if(lastX != rawX * xFactor) {
			logger.warn("Expected last X to be " + lastX + " but calculated " + rawX * xFactor);
		}
		bufferedReader.close();
		fileReader.close();
		return vendorScan;
	}
}
