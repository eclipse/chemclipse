/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.swt.widgets.Composite;
import org.swtchart.IBarSeries;
import org.swtchart.IBarSeries.BarWidthStyle;
import org.swtchart.ISeries.SeriesType;

import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public class SimpleMassSpectrumUI extends AbstractExtendedMassSpectrumUI {

	public SimpleMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massSpectrumType) {

		super(parent, style, massSpectrumType);
	}

	// -------------------------------------------------------------
	@Override
	public void setViewSeries() {

		if(this.massSpectrum != null) {
			ISeries series = SeriesConverterMSD.convertNominalMassSpectrum(this.massSpectrum, Sign.POSITIVE);
			multipleLineSeries.add(series);
			barSeries = (IBarSeries)getSeriesSet().createSeries(SeriesType.BAR, series.getId());
			barSeries.setXSeries(series.getXSeries());
			barSeries.setYSeries(series.getYSeries());
			barSeries.setBarWidthStyle(BarWidthStyle.FIXED);
			barSeries.setBarWidth(1);
			barSeries.setBarColor(Colors.RED);
		}
	}
}
