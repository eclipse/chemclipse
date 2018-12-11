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

import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.IBarSeries.BarWidthStyle;
import org.eclipse.swtchart.ISeries.SeriesType;

public class SimpleMassSpectrumUI extends AbstractExtendedMassSpectrumUI {

	public SimpleMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massSpectrumType) {
		super(parent, style, massSpectrumType);
	}

	@Override
	public void setViewSeries() {

		if(this.massSpectrum != null) {
			ISeries series = SeriesConverterMSD.convertNominalMassSpectrum(this.massSpectrum, Sign.POSITIVE);
			multipleLineSeries.add(series);
			barSeriesPositive = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, series.getId());
			barSeriesPositive.setXSeries(series.getXSeries());
			barSeriesPositive.setYSeries(series.getYSeries());
			barSeriesPositive.setBarWidthStyle(BarWidthStyle.FIXED);
			barSeriesPositive.setBarWidth(1);
			barSeriesPositive.setBarColor(Colors.RED);
		}
	}
}
