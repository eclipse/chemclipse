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
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ISeries.SeriesType;

public class SimpleMirroredMassSpectrumUI extends AbstractExtendedMassSpectrumUI {

	private IScanMSD mirroredMassSpectrum;

	public SimpleMirroredMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massSpectrumType) {
		super(parent, style, massSpectrumType);
	}

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		update(massSpectrum, massSpectrum, forceReload);
	}

	public void update(IScanMSD massSpectrum, IScanMSD mirroredMassSpectrum, boolean forceReload) {

		this.mirroredMassSpectrum = mirroredMassSpectrum;
		super.update(massSpectrum, forceReload);
	}

	// -------------------------------------------------------------
	@Override
	public void setViewSeries() {

		if(this.massSpectrum != null) {
			ISeries series = SeriesConverterMSD.convertNominalMassSpectrum(this.massSpectrum, Sign.POSITIVE);
			multipleLineSeries.add(series);
			IBarSeries barSeriesPositive = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, series.getId() + "+");
			barSeriesPositive.setXSeries(series.getXSeries());
			barSeriesPositive.setYSeries(series.getYSeries());
			barSeriesPositive.setBarWidthStyle(BarWidthStyle.FIXED);
			barSeriesPositive.setBarWidth(1);
			barSeriesPositive.setBarColor(Colors.RED);
			super.barSeriesPositive = barSeriesPositive;
			//
			if(mirroredMassSpectrum != null) {
				ISeries seriesMirrored = SeriesConverterMSD.convertNominalMassSpectrum(mirroredMassSpectrum, Sign.NEGATIVE);
				multipleLineSeries.add(seriesMirrored);
				IBarSeries barSeriesNegative = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, seriesMirrored.getId() + "-");
				barSeriesNegative.setXSeries(seriesMirrored.getXSeries());
				barSeriesNegative.setYSeries(seriesMirrored.getYSeries());
				barSeriesNegative.setBarWidthStyle(BarWidthStyle.FIXED);
				barSeriesNegative.setBarWidth(1);
				barSeriesNegative.setBarColor(Colors.BLACK);
				super.barSeriesNegative = barSeriesNegative;
			}
		} else {
			clear();
		}
	}
	// -------------------------------------------------------------
}
