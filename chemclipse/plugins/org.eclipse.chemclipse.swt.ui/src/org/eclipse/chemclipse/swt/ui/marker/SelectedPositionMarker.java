/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.ICustomPaintListener;

public class SelectedPositionMarker implements ICustomPaintListener, ISelectedPositionMarker {

	private int retentionTimeInMilliseconds;
	private double retentionTime;
	private double abundance;
	private Color foregroundColor;
	private Color backgroundColor;
	private DecimalFormat decimalFormatRetentionTime;
	private DecimalFormat decimalFormatAbundance;
	private StringBuilder stringBuilder;

	public SelectedPositionMarker(Color foregroundColor, Color backgroundColor) {
		if(foregroundColor == null || backgroundColor == null) {
			foregroundColor = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
			backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		}
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
		decimalFormatRetentionTime = ValueFormat.getDecimalFormatEnglish("0.00");
		decimalFormatAbundance = ValueFormat.getDecimalFormatEnglish("0");
	}

	@Override
	public void paintControl(PaintEvent e) {

		e.gc.setForeground(foregroundColor);
		e.gc.setBackground(backgroundColor);
		if(retentionTime > 0) {
			stringBuilder = new StringBuilder();
			stringBuilder.append("RT (min): ");
			stringBuilder.append(decimalFormatRetentionTime.format(retentionTime));
			stringBuilder.append(" AB: ");
			stringBuilder.append(decimalFormatAbundance.format(abundance));
			stringBuilder.append("\n");
			stringBuilder.append("RT (ms): ");
			stringBuilder.append(retentionTimeInMilliseconds);
			String label = stringBuilder.toString();
			e.gc.drawText(label, 10, 10);
		}
	}

	@Override
	public boolean drawBehindSeries() {

		return false;
	}

	@Override
	public void setActualPosition(int retentionTimeInMilliseconds, double abundance) {

		this.retentionTimeInMilliseconds = retentionTimeInMilliseconds;
		retentionTime = getRetentionTimeAsMinutes(retentionTimeInMilliseconds);
		this.abundance = abundance;
	}

	private double getRetentionTimeAsMinutes(int retentionTimeInMilliseconds) {

		return retentionTimeInMilliseconds / IChromatogram.MINUTE_CORRELATION_FACTOR;
	}
}
