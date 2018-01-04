/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.svg.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	private static final int SCALE_INTENSITY = 1000;

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		/*
		 * Get the SVG element.
		 */
		DOMImplementation domImplementation = GenericDOMImplementation.getDOMImplementation();
		String namespaceSVG = "http://www.w3.org/2000/svg";
		Document document = domImplementation.createDocument(namespaceSVG, "svg", null);
		SVGGraphics2D svgGraphics2D = new SVGGraphics2D(document);
		/*
		 * Parse the chromatogram.
		 */
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		int y2 = 0;
		//
		float maxSignal = chromatogram.getMaxSignal();
		Iterator<IScan> iterator = chromatogram.getScans().iterator();
		while(iterator.hasNext()) {
			IScan scan = iterator.next();
			x2 = (int)(scan.getRetentionTime() / 1000.0d); // seconds
			y2 = SCALE_INTENSITY - (int)((SCALE_INTENSITY / maxSignal) * scan.getTotalSignal()); // 0,0 is top left
			//
			svgGraphics2D.drawLine(x1, y1, x2, y2);
			//
			x1 = x2;
			y1 = y2;
		}
		/*
		 * Write the document.
		 */
		svgGraphics2D.stream(new FileWriter(file));
	}
}
