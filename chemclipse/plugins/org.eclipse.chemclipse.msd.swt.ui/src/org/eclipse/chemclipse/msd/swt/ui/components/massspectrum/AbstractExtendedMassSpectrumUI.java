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

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.model.notifier.IonSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.msd.swt.ui.exceptions.NoIonAvailableException;
import org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum.BarSeriesUtil;
import org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum.IBarSeriesIon;
import org.eclipse.chemclipse.msd.swt.ui.internal.components.massspectrum.IBarSeriesIons;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.IBarSeries;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;

public abstract class AbstractExtendedMassSpectrumUI extends AbstractViewMassSpectrumUI {

	protected IBarSeries barSeriesPositive = null;
	protected IBarSeries barSeriesNegative = null;
	//
	private static final Logger logger = Logger.getLogger(AbstractExtendedMassSpectrumUI.class);
	private Color foregroundColor;
	private static final int NUMBER_OF_IONS_TO_PAINT = 5;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	private DecimalFormat decimalFormatExact;
	/*
	 * A lot of Strings will be rendered for display.
	 * Hence, use a string builder. To create it each time would costs too much,
	 * hence, create it once and clear it before each usage.
	 */
	private StringBuilder stringBuilder;

	public AbstractExtendedMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		decimalFormatExact = ValueFormat.getDecimalFormatEnglish("0.0#####");
		//
		stringBuilder = new StringBuilder();
	}

	@Override
	public void mouseDoubleClick(MouseEvent event) {

		super.mouseDoubleClick(event);
		try {
			int widthPlotArea = getPlotArea().getBounds().width;
			double ion = BarSeriesUtil.getSelectedIon(event.x, barSeriesPositive, widthPlotArea);
			updateSelectedIon(ion);
		} catch(NoIonAvailableException e) {
			logger.warn(e);
		}
	}

	private void updateSelectedIon(double ion) {

		if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
			ChromatogramSelectionMSD actualChromatogramSelection = (ChromatogramSelectionMSD)chromatogramSelection;
			IMarkedIons selectedIons = actualChromatogramSelection.getSelectedIons();
			updateIon(selectedIons, ion);
			/*
			 * Fire an update to reload the views
			 * and to calculate e.g. formulas of the selected ion.
			 */
			ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, false);
		}
		/*
		 * Fire and update, regardless if there is a valid chromatogram selection or not.
		 * This method is used e.g. to calculate a formula from a peak mass spectrum.
		 */
		IonSelectionUpdateNotifier.fireUpdateChange(ion);
	}

	private void updateIon(IMarkedIons selectedIons, double ion) {

		switch(massValueDisplayPrecision) {
			case NOMINAL:
				updateNominalIon(selectedIons, (int)ion);
				break;
			case EXACT:
				updateExactIon(selectedIons, ion);
				break;
			case TRANSITION:
				updateExactIon(selectedIons, ion);
				break;
			default:
		}
	}

	private void updateNominalIon(IMarkedIons selectedIons, int ion) {

		assert selectedIons != null : "The selected ions instance must not be null.";
		if(selectedIons.getIonsNominal().contains(ion)) {
			selectedIons.remove(new MarkedIon(ion));
		} else {
			selectedIons.add(new MarkedIon(ion));
		}
	}

	private void updateExactIon(IMarkedIons selectedIons, double ion) {

		assert selectedIons != null : "The selected ions instance must not be null.";
		IMarkedIon markedIon = new MarkedIon(ion);
		if(selectedIons.contains(markedIon)) {
			selectedIons.remove(markedIon);
		} else {
			selectedIons.add(new MarkedIon(ion));
		}
	}

	@Override
	protected void initialize() {

		super.initialize();
		foregroundColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		IPlotArea plotArea = (IPlotArea)getPlotArea();
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			public void paintControl(PaintEvent e) {

				if(barSeriesPositive != null) {
					paintIonValues(barSeriesPositive, false, e);
				}
				//
				if(barSeriesNegative != null) {
					paintIonValues(barSeriesNegative, true, e);
				}
			}

			public boolean drawBehindSeries() {

				return false;
			}
		});
	}

	protected void paintIonValues(IBarSeries barSeries, boolean mirrored, PaintEvent e) {

		e.gc.setForeground(foregroundColor);
		List<IBarSeriesIon> barSeriesIons = getHighestXValueIndices(barSeries, mirrored);
		/*
		 * Set the label for each ion.
		 */
		for(IBarSeriesIon barSeriesIon : barSeriesIons) {
			Point point = barSeries.getPixelCoordinates(barSeriesIon.getIndex());
			String label;
			double ion = barSeriesIon.getIon();
			switch(massValueDisplayPrecision) {
				case NOMINAL:
					label = String.valueOf((int)ion);
					break;
				case EXACT:
					label = decimalFormatExact.format(ion);
					break;
				case TRANSITION:
					label = decimalFormatExact.format(ion);
					break;
				default:
					label = String.valueOf((int)ion);
			}
			/*
			 * Use transition ONLY in EXACT modus:
			 * The ion is used to get the m/z instance from the mass spectrum.
			 * If NOMINAL or ACCURATE are used, the correct m/z instance
			 * can't be fetched from the mass spectrum. That's why it is only
			 * use in exact modus.
			 */
			if(massSpectrum != null && massValueDisplayPrecision == MassValueDisplayPrecision.TRANSITION) {
				/*
				 * Extend the the label.
				 */
				try {
					IIon mz = massSpectrum.getIon(ion);
					IIonTransition ionTransition = mz.getIonTransition();
					if(ionTransition != null) {
						/*
						 * If there is a transition, use the following mode:
						 * parent ion > ion @collision energy
						 * e.g.
						 * 156 > 78.56 @12
						 */
						clearStringBuilder();
						stringBuilder.append(Integer.toString((int)ionTransition.getQ1StartIon())); // 156
						stringBuilder.append(" > ");
						stringBuilder.append(label); // 78.56
						stringBuilder.append(" @");
						stringBuilder.append(Integer.toString((int)ionTransition.getCollisionEnergy())); // 12
						label = stringBuilder.toString();
					}
				} catch(AbundanceLimitExceededException e1) {
					logger.warn(e1);
				} catch(IonLimitExceededException e1) {
					logger.warn(e1);
				}
			}
			/*
			 * Draw the label
			 */
			Point labelSize = e.gc.textExtent(label);
			int x = (int)(point.x + barSeries.getBarWidth() / 2d - labelSize.x / 2d);
			int y = point.y;
			if(!mirrored) {
				y = point.y - labelSize.y;
			}
			e.gc.drawText(label, x, y, true);
		}
	}

	/**
	 * Clears the string builder.
	 */
	private void clearStringBuilder() {

		stringBuilder.delete(0, stringBuilder.length());
	}

	private List<IBarSeriesIon> getHighestXValueIndices(IBarSeries barSeries, boolean mirrored) {

		int numberOfIonsToPaint = PreferenceSupplier.getScanDisplayNumberOfIons();
		if(numberOfIonsToPaint < 0) {
			numberOfIonsToPaint = NUMBER_OF_IONS_TO_PAINT;
		}
		int widthPlotArea = getPlotArea().getBounds().width;
		IBarSeriesIons barSeriesIons = BarSeriesUtil.getBarSeriesIonList(barSeries, widthPlotArea);
		/*
		 * Decide which method to use.
		 */
		List<IBarSeriesIon> barSeriesIonList;
		if(PreferenceSupplier.isUseModuloDisplayNumberOfIons()) {
			barSeriesIonList = barSeriesIons.getIonsByModulo(numberOfIonsToPaint, mirrored);
		} else {
			barSeriesIonList = barSeriesIons.getIonsWithHighestAbundance(numberOfIonsToPaint, mirrored);
		}
		//
		return barSeriesIonList;
	}

	protected ISeries getSeries(IScanMSD massSpectrum) {

		ISeries series;
		switch(massValueDisplayPrecision) {
			case NOMINAL:
				series = SeriesConverterMSD.convertNominalMassSpectrum(massSpectrum, Sign.POSITIVE);
				break;
			case EXACT:
				series = SeriesConverterMSD.convertExactMassSpectrum(massSpectrum, Sign.POSITIVE);
				break;
			case TRANSITION:
				series = SeriesConverterMSD.convertExactMassSpectrum(massSpectrum, Sign.POSITIVE);
				break;
			default:
				series = SeriesConverterMSD.convertNominalMassSpectrum(massSpectrum, Sign.POSITIVE);
		}
		return series;
	}
}
