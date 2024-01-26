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
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.pdf.support;

import org.apache.pdfbox.pdmodel.PDPage;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;

public class ValueScaling {

	private int startTime;
	private int stopTime;
	private float minSignal;
	private float maxSignal;
	private float pageWidthLandscape;
	private float pageHeightLandscape;
	//
	private float topMargin = 50;
	private float bottomMargin = 50;
	private float leftMargin = 50;
	private float rightMargin = 50;

	public ValueScaling(IChromatogram<? extends IPeak> chromatogram, PDPage page, ITotalScanSignals scans) {

		startTime = scans.getFirstTotalScanSignal().getRetentionTime();
		stopTime = scans.getLastTotalScanSignal().getRetentionTime();
		minSignal = chromatogram.getMinSignal();
		maxSignal = chromatogram.getMaxSignal();
		pageHeightLandscape = page.getMediaBox().getWidth();
		pageWidthLandscape = page.getMediaBox().getHeight();
	}

	public float getY(float intensity) {

		float span = maxSignal - minSignal;
		float drawableHeight = pageHeightLandscape - (topMargin + bottomMargin);
		return topMargin + ((intensity - minSignal) / span) * drawableHeight;
	}

	public float getX(int time) {

		float duration = stopTime - startTime;
		float drawableWidth = pageWidthLandscape - (leftMargin + rightMargin);
		return leftMargin + ((time - startTime) / duration) * drawableWidth;
	}
}