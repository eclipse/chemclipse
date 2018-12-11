/*******************************************************************************
 * Copyright (c) 2016, 2018 Matthias Mailänder.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.ISeries.SeriesType;

public class SimpleContinuousMassSpectrumUI extends AbstractExtendedMassSpectrumUI {

	public SimpleContinuousMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massSpectrumType) {
		super(parent, style, massSpectrumType);
	}

	// -------------------------------------------------------------
	@Override
	public void setViewSeries() {

		if(this.massSpectrum != null) {
			ISeries series = SeriesConverterMSD.convertNominalMassSpectrum(this.massSpectrum, Sign.POSITIVE);
			multipleLineSeries.add(series);
			ILineSeries lineSeries;
			//
			lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(false);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			lineSeries.setLineColor(Colors.RED);
		}
	}
}
