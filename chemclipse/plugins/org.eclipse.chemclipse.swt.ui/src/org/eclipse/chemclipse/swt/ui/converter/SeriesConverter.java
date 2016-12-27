/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.ChromatogramSelection;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.swt.ui.exceptions.NoPeaksAvailableException;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public class SeriesConverter {

	public static final Logger logger = Logger.getLogger(SeriesConverter.class);

	/**
	 * Use only static methods.
	 */
	private SeriesConverter() {
	}

	/*
	 * Why is implemented, that the List<ISeries> convert method have a
	 * parameter IOffset offset and the normal methods not?<br/> My aim was,
	 * that a normal e.g. chromatogram will not be presented in an offset mode.
	 * But if you have several chromatograms, you may want to show them in an
	 * offset mode.
	 */
	/**
	 * @param chromatogramOverview
	 * @param sign
	 */
	public static ISeries convertChromatogramOverview(IChromatogramOverview chromatogramOverview, Sign sign, boolean validatePositive) {

		List<IChromatogramOverview> chromatogramOverviews = new ArrayList<IChromatogramOverview>();
		chromatogramOverviews.add(chromatogramOverview);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries chromatogramSeries = convertChromatogramOverviews(chromatogramOverviews, sign, offset, validatePositive);
		return chromatogramSeries.getMultipleSeries().get(0);
	}

	/**
	 * @param chromatogramOverviews
	 * @param sign
	 * @param offset
	 * @return
	 */
	public static IMultipleSeries convertChromatogramOverviews(List<IChromatogramOverview> chromatogramOverviews, Sign sign, IOffset offset, boolean validatePositive) {

		IMultipleSeries chromatogramSeries = new MultipleSeries();
		if(chromatogramOverviews != null) {
			offset = validateOffset(offset);
			/*
			 * Iterate through all chromatogram overviews.
			 */
			for(IChromatogramOverview chromatogramOverview : chromatogramOverviews) {
				/*
				 * Get the signals
				 */
				try {
					ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogramOverview);
					ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(validatePositive);
					/*
					 * Pre-optimization
					 */
					// Rectangle clientArea = Display.getCurrent().getClientArea();
					// int scansDisplay = clientArea.width;
					int numberOfScans = signals.size();
					// int modulo = 0;
					// if(numberOfScans > scansDisplay && scansDisplay > 0) {
					// int factor = numberOfScans / scansDisplay;
					// if(factor >= 2) {
					// modulo = factor;
					// numberOfScans /= modulo;
					// }
					// }
					//
					double[] xSeries = new double[numberOfScans];
					double[] ySeries = new double[numberOfScans];
					int x = 0;
					int y = 0;
					double retentionTime;
					double abundance;
					double xOffset;
					double yOffset;
					/*
					 * Retrieve the chromatogram x and y signals.
					 */
					// int i = 1;
					for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
						/*
						 * Pre-optimization
						 */
						// if(modulo > 0) {
						// if((i % modulo) != 0) {
						// i++;
						// continue;
						// }
						// }
						// i++;
						retentionTime = signal.getRetentionTime();
						abundance = signal.getTotalSignal();
						xOffset = offset.getCurrentXOffset();
						yOffset = offset.getCurrentYOffset();
						/*
						 * Sign the abundance as a negative value?
						 */
						if(sign == Sign.NEGATIVE) {
							abundance *= -1;
							xOffset *= -1;
							yOffset *= -1;
						}
						/*
						 * Set the offset.
						 */
						retentionTime += xOffset;
						abundance += yOffset;
						/*
						 * Store the values in the array.
						 */
						xSeries[x++] = retentionTime;
						ySeries[y++] = abundance;
					}
					/*
					 * Increment the offset.
					 */
					offset.incrementCurrentXOffset();
					offset.incrementCurrentYOffset();
					chromatogramSeries.add(new Series(xSeries, ySeries, chromatogramOverview.getName()));
				} catch(ChromatogramIsNullException e) {
					logger.warn(e);
				}
			}
		}
		return chromatogramSeries;
	}

	public static IMultipleSeries convertChromatograms(List<IChromatogramSelection> chromatogramSelections, Sign sign, IOffset offset, boolean validatePositive) {

		return convertChromatograms(chromatogramSelections, sign, offset, false, validatePositive);
	}

	/**
	 * Returns the chromatogram selections as series.
	 * 
	 * @param chromatograms
	 * @param sign
	 * @param offset
	 * @return IMultipleSeries
	 */
	public static IMultipleSeries convertChromatograms(List<IChromatogramSelection> chromatogramSelections, Sign sign, IOffset offset, boolean useLockedOffset, boolean validatePositive) {

		/*
		 * There must be at least one chromatogram in the list.
		 */
		IMultipleSeries chromatogramSeries = new MultipleSeries();
		if(chromatogramSelections != null && chromatogramSelections.size() >= 1) {
			/*
			 * Validate Offset
			 */
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * The first chromatogram is the master.
			 * It determines the retention time range.<br/>
			 * The master determines the scan range size for SWTChart.
			 */
			int masterStartRetentionTime = chromatogramSelections.get(0).getStartRetentionTime();
			int masterStopRetentionTime = chromatogramSelections.get(0).getStopRetentionTime();
			/*
			 * Iterate through all chromatogram selections.
			 */
			int counter = 0;
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				/*
				 * Is the offset locked?
				 */
				boolean isLockOffset = false;
				if(useLockedOffset) {
					isLockOffset = chromatogramSelection.isLockOffset();
				}
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				counter++;
				/*
				 * Get the scan range of the master chromatogram.
				 */
				int startScan = getStartScan(masterStartRetentionTime, chromatogram);
				int stopScan = getStopScan(masterStopRetentionTime, chromatogram);
				/*
				 * If the start or stop scan is zero, continue with the next chromatogram.
				 */
				if(startScan == 0 || stopScan == 0) {
					continue;
				}
				/*
				 * Total signals
				 */
				ITotalScanSignals signals = getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive);
				if(signals == null) {
					continue;
				}
				int scans = getSizeWithRetentionTimeGreatherThanZero(signals); // signals.size() contains zero values.
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				double retentionTime;
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Retrieve the chromatogram x and y signals.
				 */
				for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
					retentionTime = signal.getRetentionTime();
					abundance = signal.getTotalSignal();
					/*
					 * To avoid strange chromatogram views.
					 * getSizeWithRetentionTimeGreatherThanZero(signals):
					 * the arrays xSeries, ySeries do not have space for zero scans
					 */
					if(retentionTime == 0) {
						continue;
					}
					/*
					 * Sign the abundance as a negative value?
					 */
					if(isLockOffset) {
						xOffset = chromatogramSelection.getOffset().getX();
						yOffset = chromatogramSelection.getOffset().getY();
					} else {
						xOffset = offset.getCurrentXOffset();
						yOffset = offset.getCurrentYOffset();
					}
					if(sign == Sign.NEGATIVE) {
						abundance *= -1;
						xOffset *= -1;
						yOffset *= -1;
					}
					/*
					 * Set the offset.
					 */
					retentionTime += xOffset;
					abundance += yOffset;
					/*
					 * Store the values in the array.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				chromatogramSeries.add(new Series(xSeries, ySeries, "[" + counter + "] " + chromatogram.getName()));
			}
		}
		return chromatogramSeries;
	}

	/**
	 * Returns the chromatogram selections as series.
	 * 
	 * @param chromatograms
	 * @param sign
	 * @param offset
	 * @return IMultipleSeries
	 */
	public static IMultipleSeries convertChromatogramsMirrored(List<IChromatogramSelection> chromatogramSelections, IOffset offset, boolean validatePositive) {

		/*
		 * There must be at least one chromatogram in the list.
		 */
		IMultipleSeries chromatogramSeries = new MultipleSeries();
		if(chromatogramSelections != null && chromatogramSelections.size() >= 1) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * The first chromatogram is the master.
			 * It determines the retention time range.<br/>
			 * The master determines the scan range size for SWTChart.
			 */
			int masterStartRetentionTime = chromatogramSelections.get(0).getStartRetentionTime();
			int masterStopRetentionTime = chromatogramSelections.get(0).getStopRetentionTime();
			/*
			 * Iterate through all chromatogram selections.
			 */
			int counter = 0;
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				counter++;
				/*
				 * Get the scan range of the master chromatogram.
				 */
				int startScan = getStartScan(masterStartRetentionTime, chromatogram);
				int stopScan = getStopScan(masterStopRetentionTime, chromatogram);
				/*
				 * If the start or stop scan is zero, continue with the next chromatogram.
				 */
				if(startScan == 0 || stopScan == 0) {
					continue;
				}
				/*
				 * Total signals
				 */
				ITotalScanSignals signals = getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive);
				if(signals == null) {
					continue;
				}
				int scans = getSizeWithRetentionTimeGreatherThanZero(signals); // signals.size() contains zero values.
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				double retentionTime;
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Retrieve the chromatogram x and y signals.
				 */
				for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
					retentionTime = signal.getRetentionTime();
					abundance = signal.getTotalSignal();
					/*
					 * To avoid strange chromatogram views.
					 * getSizeWithRetentionTimeGreatherThanZero(signals):
					 * the arrays xSeries, ySeries do not have space for zero scans
					 */
					if(retentionTime == 0) {
						continue;
					}
					/*
					 * Sign the abundance as a negative value?
					 */
					xOffset = offset.getCurrentXOffset();
					yOffset = offset.getCurrentYOffset();
					// Negative mode
					if(counter > 1) {
						abundance *= -1;
						xOffset *= -1;
						yOffset *= -1;
					}
					/*
					 * Set the offset.
					 */
					retentionTime += xOffset;
					abundance += yOffset;
					/*
					 * Store the values in the array.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				chromatogramSeries.add(new Series(xSeries, ySeries, "[" + counter + "] " + chromatogram.getName()));
			}
		}
		return chromatogramSeries;
	}

	/**
	 * Returns the chromatogram selections as series.
	 * 
	 * @param chromatograms
	 * @param sign
	 * @param offset
	 * @return IMultipleSeries
	 */
	public static IMultipleSeries convertChromatogramsSubtracted(List<IChromatogramSelection> chromatogramSelections, IOffset offset, boolean validatePositive) {

		/*
		 * There must be at least one chromatogram in the list.
		 */
		IMultipleSeries chromatogramSeries = new MultipleSeries();
		if(chromatogramSelections != null && chromatogramSelections.size() >= 1) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * The first chromatogram is the master.
			 * It determines the retention time range.<br/>
			 * The master determines the scan range size for SWTChart.
			 */
			IChromatogramSelection chromatogramSelectionMaster = chromatogramSelections.get(0);
			IChromatogram chromatogramMaster = chromatogramSelectionMaster.getChromatogram();
			int masterStartRetentionTime = chromatogramSelectionMaster.getStartRetentionTime();
			int masterStopRetentionTime = chromatogramSelectionMaster.getStopRetentionTime();
			/*
			 * Iterate through all chromatogram selections and subtract each from the master.
			 */
			for(int i = 1; i < chromatogramSelections.size(); i++) {
				/*
				 * Chromatogram i = 0 is the master.
				 */
				IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				/*
				 * Get the scan range of the master chromatogram.
				 */
				int startScan = getStartScan(masterStartRetentionTime, chromatogram);
				int stopScan = getStopScan(masterStopRetentionTime, chromatogram);
				/*
				 * If the start or stop scan is zero, continue with the next chromatogram.
				 */
				if(startScan == 0 || stopScan == 0) {
					continue;
				}
				/*
				 * Total signals
				 */
				ITotalScanSignals signals = getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive);
				if(signals == null) {
					continue;
				}
				//
				int scans = getSizeWithRetentionTimeGreatherThanZero(signals); // signals.size() contains zero values.
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				double retentionTime;
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Retrieve the chromatogram x and y signals.
				 */
				for(int scan = startScan; scan <= stopScan; scan++) {
					//
					ITotalScanSignal signal = signals.getTotalScanSignal(scan);
					retentionTime = signal.getRetentionTime();
					/*
					 * To avoid strange chromatogram views.
					 * getSizeWithRetentionTimeGreatherThanZero(signals):
					 * the arrays xSeries, ySeries do not have space for zero scans
					 */
					if(retentionTime == 0) {
						continue;
					}
					//
					int scanNumberMaster = chromatogramMaster.getScanNumber(signal.getRetentionTime());
					IScan scanMaster = chromatogramMaster.getScan(scanNumberMaster);
					if(scanMaster == null) {
						continue;
					}
					//
					abundance = scanMaster.getTotalSignal() - signal.getTotalSignal();
					/*
					 * Sign the abundance as a negative value?
					 */
					xOffset = offset.getCurrentXOffset();
					yOffset = offset.getCurrentYOffset();
					/*
					 * Set the offset.
					 */
					retentionTime += xOffset;
					abundance += yOffset;
					/*
					 * Store the values in the array.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				chromatogramSeries.add(new Series(xSeries, ySeries, "[" + i + "] " + chromatogramMaster.getName() + " - " + chromatogram.getName()));
			}
		}
		return chromatogramSeries;
	}

	public static ISeries convertChromatogram(IChromatogramSelection chromatogramSelection, Sign sign, boolean validatePositive) {

		return convertChromatogram(chromatogramSelection, sign, false, validatePositive);
	}

	/**
	 * Returns the given chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param sign
	 * @return ISeries
	 */
	public static ISeries convertChromatogram(IChromatogramSelection chromatogramSelection, Sign sign, boolean useLockedOffset, boolean validatePositive) {

		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		chromatogramSelections.add(chromatogramSelection);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries chromatogramSelectionSeries = convertChromatograms(chromatogramSelections, sign, offset, useLockedOffset, validatePositive);
		return chromatogramSelectionSeries.getMultipleSeries().get(0);
	}

	/**
	 * Calculates a derivative 1st, 2nd, 3rd of a given chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @param sign
	 * @param validatePositive
	 * @return ISeries
	 */
	public static IMultipleSeries convertDerivativeChromatogram(IChromatogramSelection chromatogramSelection, int derivative, int scale, boolean validatePositive) {

		IMultipleSeries multipleSeries = new MultipleSeries();
		int masterStartRetentionTime = chromatogramSelection.getStartRetentionTime();
		int masterStopRetentionTime = chromatogramSelection.getStopRetentionTime();
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		/*
		 * Get the scan range of the master chromatogram.
		 */
		int startScan = getStartScan(masterStartRetentionTime, chromatogram);
		int stopScan = getStopScan(masterStopRetentionTime, chromatogram);
		ITotalScanSignals signals = getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive);
		if(signals != null) {
			/*
			 * Create the master series.
			 */
			int scans = getSizeWithRetentionTimeGreatherThanZero(signals);
			double[] xSeries = new double[scans];
			double[] ySeries = new double[scans];
			int x = 0;
			int y = 0;
			double retentionTime;
			double abundance = 0;
			/*
			 * Retrieve the chromatogram x and y signals.
			 */
			for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
				retentionTime = signal.getRetentionTime();
				abundance = signal.getTotalSignal();
				/*
				 * To avoid strange chromatogram views.
				 * getSizeWithRetentionTimeGreatherThanZero(signals):
				 * the arrays xSeries, ySeries do not have space for zero scans
				 */
				if(retentionTime == 0) {
					continue;
				}
				/*
				 * Store the values in the array.
				 */
				xSeries[x++] = retentionTime;
				ySeries[y++] = abundance;
			}
			multipleSeries.add(new Series(xSeries, ySeries, chromatogram.getName()));
			/*
			 * Create the derivative series.
			 */
			double[] ySeriesBase = ySeries;
			double[] ySeriesDerivative = new double[scans];
			for(int i = 0; i < derivative; i++) {
				ySeriesDerivative = new double[scans];
				for(int j = 1; j < scans; j++) {
					ySeriesDerivative[j] = (ySeriesBase[j] - ySeriesBase[j - 1]) * scale;
				}
				ySeriesBase = ySeriesDerivative;
			}
			multipleSeries.add(new Series(xSeries, ySeriesDerivative, derivative + ". Derivative scale[" + scale + "]"));
		}
		return multipleSeries;
	}

	public static ISeries convertChromatogram(IChromatogram chromatogram, Sign sign, boolean validatePositive) {

		return convertChromatogram(chromatogram, sign, false, validatePositive);
	}

	/**
	 * Returns a series instance of the given chromatogram.
	 * 
	 * @param chromatogram
	 * @param sign
	 * @return
	 */
	public static ISeries convertChromatogram(IChromatogram chromatogram, Sign sign, boolean useLockedOffset, boolean validatePositive) {

		ISeries series = null;
		IChromatogramSelection chromatogramSelection;
		try {
			chromatogramSelection = new ChromatogramSelection(chromatogram);
			series = convertChromatogram(chromatogramSelection, sign, useLockedOffset, validatePositive);
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		return series;
	}

	/**
	 * Converts the baseline given by the range of chromatogramSelection to an
	 * instance of ISeries.
	 * 
	 * @param chromatogramSelection
	 * @param sign
	 */
	public static ISeries convertBaseline(IChromatogramSelection chromatogramSelection, Sign sign, boolean validatePositive) {

		Series baselineSeries = null;
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			IBaselineModel baselineModel = chromatogram.getBaselineModel();
			int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
			ITotalScanSignals signals = getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive);
			if(signals != null) {
				int scans = signals.size();
				double[] xSeries = new double[scans];
				double[] ySeries = new double[scans];
				int x = 0;
				int y = 0;
				double abundance;
				/*
				 * Retrieve the baseline x and y signals.
				 */
				for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
					xSeries[x++] = signal.getRetentionTime();
					abundance = baselineModel.getBackgroundAbundance(signal.getRetentionTime());
					if(sign == Sign.NEGATIVE) {
						abundance *= -1;
					}
					ySeries[y++] = abundance;
				}
				baselineSeries = new Series(xSeries, ySeries, "Baseline");
			}
		}
		return baselineSeries;
	}

	/**
	 * Validates the offset instance.<br/>
	 * If null, a valid instance will be created.
	 * 
	 * @param offset
	 */
	public static IOffset validateOffset(IOffset offset) {

		if(offset == null) {
			offset = new Offset(0, 0);
		}
		return offset;
	}

	/**
	 * Returns the total scan signals.
	 * May return null.
	 * 
	 * @param chromatogram
	 * @param startScan
	 * @param stopScan
	 * @return {@link ITotalScanSignals}
	 */
	public static ITotalScanSignals getTotalScanSignals(IChromatogram chromatogram, int startScan, int stopScan, boolean validatePositive) {

		if(PreferenceSupplier.condenseCycleNumberScans() && chromatogram.containsScanCycles()) {
			return getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive, true);
		} else {
			return getTotalScanSignals(chromatogram, startScan, stopScan, validatePositive, false);
		}
	}

	private static ITotalScanSignals getTotalScanSignals(IChromatogram chromatogram, int startScan, int stopScan, boolean validatePositive, boolean condenseCycleNumberScans) {

		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(startScan, stopScan, validatePositive, condenseCycleNumberScans);
			return signals;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
			return null;
		}
	}

	/**
	 * Returns the correct start scan.
	 * If there is no suitable scan, 0 will be returned.
	 * 
	 * @param masterStartRetentionTime
	 * @param chromatogram
	 * @return int
	 */
	public static int getStartScan(int masterStartRetentionTime, IChromatogram chromatogram) {

		assert chromatogram != null : "The chromatogram must be not null.";
		int scan = 0;
		int startRetentionTime = chromatogram.getStartRetentionTime();
		int stopRetentionTime = chromatogram.getStopRetentionTime();
		if(masterStartRetentionTime <= startRetentionTime) {
			scan = 1;
		} else if(masterStartRetentionTime > startRetentionTime && masterStartRetentionTime <= stopRetentionTime) {
			scan = chromatogram.getScanNumber(masterStartRetentionTime);
		}
		return scan;
	}

	/**
	 * Returns the correct stop scan.
	 * If there is no suitable scan, 0 will be returned.
	 * 
	 * @param masterStopRetentionTime
	 * @param chromatogram
	 * @return int
	 */
	public static int getStopScan(int masterStopRetentionTime, IChromatogram chromatogram) {

		assert chromatogram != null : "The chromatogram must be not null.";
		int scan = 0;
		int startRetentionTime = chromatogram.getStartRetentionTime();
		int stopRetentionTime = chromatogram.getStopRetentionTime();
		if(masterStopRetentionTime >= stopRetentionTime) {
			scan = chromatogram.getNumberOfScans();
		} else if(masterStopRetentionTime < stopRetentionTime && masterStopRetentionTime >= startRetentionTime) {
			scan = chromatogram.getScanNumber(masterStopRetentionTime);
		}
		return scan;
	}

	public static int getSizeWithRetentionTimeGreatherThanZero(ITotalScanSignals signals) {

		int counter = 0;
		for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
			if(signal.getRetentionTime() > 0) {
				counter++;
			}
		}
		return counter;
	}

	public static ISeries convertSelectedPeak(IPeak peak, boolean includeBackground, Sign sign) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeaks(peaks, includeBackground, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	public static ISeries convertSelectedPeakBackground(IPeak peak, Sign sign) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeakBackground(peaks, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	/**
	 * Returns an ISeries instance of the given peak.
	 * 
	 * @param peak
	 * @param includeBackground
	 * @param sign
	 */
	public static ISeries convertPeak(IPeak peak, boolean includeBackground, Sign sign) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeaks(peaks, includeBackground, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	// TODO JUnit
	public static ISeries convertPeakBackground(IPeak peak, Sign sign) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.add(peak);
		IOffset offset = new Offset(0, 0);
		IMultipleSeries peakSeries = convertPeakBackground(peaks, sign, offset);
		return peakSeries.getMultipleSeries().get(0);
	}

	/**
	 * Returns a list of peaks with a given sign, and a given offset.<br/>
	 * You can also choose if you would like to include the background.
	 * 
	 * @param peaks
	 * @param includeBackground
	 * @param sign
	 * @param offset
	 * @return List<ISeries>
	 */
	public static IMultipleSeries convertPeaks(List<? extends IPeak> peaks, boolean includeBackground, Sign sign, IOffset offset) {

		IMultipleSeries peakSeries = new MultipleSeries();
		if(peaks != null) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * Convert each peak to a series.
			 */
			for(IPeak peak : peaks) {
				/*
				 * Continue if the actual peak is null.
				 */
				if(peak == null) {
					continue;
				}
				IPeakModel peakModel = peak.getPeakModel();
				/*
				 * Initialize with zero.
				 */
				int size = peakModel.getRetentionTimes().size();
				double[] xSeries = new double[size];
				double[] ySeries = new double[size];
				int x = 0;
				int y = 0;
				/*
				 * Values.
				 */
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Go through all retention times of the peak.
				 */
				for(int retentionTime : peakModel.getRetentionTimes()) {
					abundance = peakModel.getPeakAbundance(retentionTime);
					/*
					 * Include the background?
					 */
					if(includeBackground) {
						abundance += peakModel.getBackgroundAbundance(retentionTime);
					}
					/*
					 * Sign the abundance as a negative value?
					 */
					xOffset = offset.getCurrentXOffset();
					yOffset = offset.getCurrentYOffset();
					if(sign == Sign.NEGATIVE) {
						abundance *= -1;
						xOffset *= -1;
						yOffset *= -1;
					}
					/*
					 * Set the offset.
					 */
					retentionTime += xOffset;
					abundance += yOffset;
					/*
					 * Set the values.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				peakSeries.add(new Series(xSeries, ySeries, "Peak"));
			}
		}
		return peakSeries;
	}

	public static IMultipleSeries convertPeakBackground(List<? extends IPeak> peaks, Sign sign, IOffset offset) {

		IMultipleSeries peakBackgroundSeries = new MultipleSeries();
		if(peaks != null) {
			offset = SeriesConverter.validateOffset(offset);
			/*
			 * Convert each peak to a series.
			 */
			for(IPeak peak : peaks) {
				/*
				 * Continue if the actual peak is null.
				 */
				if(peak == null) {
					continue;
				}
				IPeakModel peakModel = peak.getPeakModel();
				/*
				 * Initialize with zero.
				 */
				int size = peakModel.getRetentionTimes().size();
				double[] xSeries = new double[size];
				double[] ySeries = new double[size];
				int x = 0;
				int y = 0;
				/*
				 * Values.
				 */
				double abundance;
				double xOffset;
				double yOffset;
				/*
				 * Go through all retention times of the peak.
				 */
				for(int retentionTime : peakModel.getRetentionTimes()) {
					abundance = peakModel.getBackgroundAbundance(retentionTime);
					/*
					 * Sign the abundance as a negative value?
					 */
					xOffset = offset.getCurrentXOffset();
					yOffset = offset.getCurrentYOffset();
					if(sign == Sign.NEGATIVE) {
						abundance *= -1;
						xOffset *= -1;
						yOffset *= -1;
					}
					/*
					 * Set the offset.
					 */
					retentionTime += xOffset;
					abundance += yOffset;
					/*
					 * Set the values.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
				/*
				 * Increment the offset.
				 */
				offset.incrementCurrentXOffset();
				offset.incrementCurrentYOffset();
				peakBackgroundSeries.add(new Series(xSeries, ySeries, "Background"));
			}
		}
		return peakBackgroundSeries;
	}

	/**
	 * Returns the chromatogram selections as series.
	 * 
	 * @param chromatograms
	 * @param sign
	 * @param offset
	 * @return IMultipleSeries
	 */
	public static IMultipleSeries convertPeakMaxMarker(List<? extends IPeak> peaks, Sign sign, IOffset offset, boolean activeForAnalysis) throws NoPeaksAvailableException {

		/*
		 * There must be at least one chromatogram in the list.
		 */
		IMultipleSeries peakSeries = new MultipleSeries();
		if(peaks != null) {
			offset = SeriesConverter.validateOffset(offset);
			int amountPeaks = getAmountPeaks(peaks, activeForAnalysis);
			//
			/*
			 * Throw an exception if no peaks are available.
			 */
			if(amountPeaks == 0) {
				throw new NoPeaksAvailableException();
			}
			/*
			 * Get the retention time and max abundance value for each peak.
			 */
			;
			double[] xSeries = new double[amountPeaks];
			double[] ySeries = new double[amountPeaks];
			int x = 0;
			int y = 0;
			/*
			 * Iterate through all peaks of the chromatogram selection.
			 */
			for(IPeak peak : peaks) {
				/*
				 * Retrieve the x and y signal of each peak.
				 */
				if(printPeak(peak, activeForAnalysis)) {
					IPeakModel peakModel = peak.getPeakModel();
					double retentionTime = peakModel.getRetentionTimeAtPeakMaximum();
					double abundance = peakModel.getBackgroundAbundance() + peakModel.getPeakAbundance();
					/*
					 * Sign the abundance as a negative value?
					 */
					double xOffset = offset.getCurrentXOffset();
					double yOffset = offset.getCurrentYOffset();
					if(sign == Sign.NEGATIVE) {
						abundance *= -1;
						xOffset *= -1;
						yOffset *= -1;
					}
					/*
					 * Set the offset.
					 */
					retentionTime += xOffset;
					abundance += yOffset;
					/*
					 * Store the values in the array.
					 */
					xSeries[x++] = retentionTime;
					ySeries[y++] = abundance;
				}
			}
			/*
			 * Add the series.
			 */
			if(activeForAnalysis) {
				peakSeries.add(new Series(xSeries, ySeries, "Active Peaks"));
			} else {
				peakSeries.add(new Series(xSeries, ySeries, "Inactive Peaks"));
			}
		}
		return peakSeries;
	}

	private static int getAmountPeaks(List<? extends IPeak> peaks, boolean activeForAnalysis) {

		int amountPeaks = 0;
		for(IPeak peak : peaks) {
			if(printPeak(peak, activeForAnalysis)) {
				amountPeaks++;
			}
		}
		return amountPeaks;
	}

	private static boolean printPeak(IPeak peak, boolean activeForAnalysis) {

		return activeForAnalysis == peak.isActiveForAnalysis();
	}
}
