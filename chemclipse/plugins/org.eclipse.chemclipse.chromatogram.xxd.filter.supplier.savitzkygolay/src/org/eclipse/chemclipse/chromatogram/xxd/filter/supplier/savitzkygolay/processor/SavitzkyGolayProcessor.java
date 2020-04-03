/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - Ion-wise msd chromatogram filter
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.StatUtils;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class SavitzkyGolayProcessor {

	@SuppressWarnings("unchecked")
	public static IChromatogramFilterResult smooth(IChromatogramSelection chromatogramSelection, boolean validatePositive, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		IChromatogramFilterResult chromatogramFilterResult;
		try {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			TotalScanSignalExtractor signalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals totalScanSignals = signalExtractor.getTotalScanSignals(chromatogramSelection, validatePositive);
			//
			double[] sgTic = smooth(totalScanSignals, filterSettings, monitor);
			int i = 0;
			for(ITotalScanSignal signal : totalScanSignals.getTotalScanSignals()) {
				signal.setTotalSignal((float)sgTic[i++]);
			}
			/*
			 * Apply the smoothed data to the chromatogram selection.
			 */
			int startScan = totalScanSignals.getStartScan();
			int stopScan = totalScanSignals.getStopScan();
			//
			for(int scan = startScan; scan <= stopScan; scan++) {
				monitor.subTask("Set Savitzky-Golay TIC: " + scan);
				IScan scanRecord = chromatogram.getScan(scan);
				/*
				 * A value of 0 is not allowed in case of MSD data.
				 * If it's FID data, it's possible to have negative values.
				 */
				float intensity = totalScanSignals.getTotalScanSignal(scan).getTotalSignal();
				if(validatePositive && intensity <= 0.0f) {
					intensity = 0.1f;
				}
				scanRecord.adjustTotalSignal(intensity);
			}
			//
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been applied successfully.");
		} catch(ChromatogramIsNullException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, "Something has gone wrong to apply the Savitzky-Golay filter.");
		}
		return chromatogramFilterResult;
	}

	public static double[] smooth(double[] ticValues, int derivative, int order, int width, IProgressMonitor monitor) {

		return smoothValues(ticValues, derivative, order, width);
	}

	public static double[] smooth(ITotalScanSignals totalScanSignals, int derivative, int order, int width, IProgressMonitor monitor) {

		int size = totalScanSignals.size();
		double[] ticValues = new double[size];
		int column = 0;
		for(ITotalScanSignal signal : totalScanSignals.getTotalScanSignals()) {
			ticValues[column++] = signal.getTotalSignal();
		}
		//
		return smoothValues(ticValues, derivative, order, width);
	}

	public static double[] smooth(double[] ticValues, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		int derivative = filterSettings.getDerivative();
		int order = filterSettings.getOrder();
		int width = filterSettings.getWidth();
		return smoothValues(ticValues, derivative, order, width);
	}

	public static double[] smooth(ITotalScanSignals totalScanSignals, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		int size = totalScanSignals.size();
		double[] ticValues = new double[size];
		int column = 0;
		for(ITotalScanSignal signal : totalScanSignals.getTotalScanSignals()) {
			ticValues[column++] = signal.getTotalSignal();
		}
		//
		int derivative = filterSettings.getDerivative();
		int order = filterSettings.getOrder();
		int width = filterSettings.getWidth();
		return smoothValues(ticValues, derivative, order, width);
	}

	private static double[] smoothValues(double[] ticValues, int derivative, int order, int width) {

		int p = calculateP(width);
		//
		int size = ticValues.length;
		width = Math.max(PreferenceSupplier.MIN_WIDTH, (1 + 2 * Math.round((width - 1) / 2)));
		order = (int)StatUtils.min(new double[]{Math.max(0, order), 5, (width - 1)});
		derivative = Math.min(Math.max(0, derivative), order);
		/*
		 * Get the coefficients and apply the filter.
		 */
		RealMatrix x = getX(width, order);
		double[] coefficients = calculateCoefficient(derivative, order);
		double[][] weights = getWeights(width, order, derivative);
		double[] middleWeights = weights[derivative];
		double[] newTicValues = new double[size];
		//
		double[][] startStopWeights = new double[order - derivative + 1][weights[0].length]; // rows, columns
		for(int i = derivative, k = 0; i <= order; i++, k++) {
			for(int j = 0; j < weights[0].length; j++) {
				startStopWeights[k][j] = weights[i][j];
			}
		}
		//
		for(int i = 0; i < coefficients.length; i++) {
			double coefficient = coefficients[i];
			for(int j = 0; j < startStopWeights[i].length; j++) {
				startStopWeights[i][j] *= coefficient;
			}
		}
		/*
		 * Process
		 */
		processStart(p, order, derivative, width, x, ticValues, newTicValues, startStopWeights);
		processMiddle(p, ticValues, newTicValues, middleWeights, coefficients);
		processEnd(p, order, derivative, width, x, ticValues, newTicValues, startStopWeights);
		//
		return newTicValues;
	}

	private static void processStart(int p, int order, int derivative, int width, RealMatrix x, double[] ticValues, double[] newTicValues, double[][] startStopWeights) {

		double[][] uStart = new double[p][order - derivative + 1]; // rows, columns
		for(int i = 0; i < p; i++) {
			for(int j = 0; j < order - derivative + 1; j++) {
				uStart[i][j] = x.getEntry(i, j);
			}
		}
		//
		for(int i = 0; i < p; i++) {
			double[] values = new double[width];
			for(int j = 0; j < width; j++) {
				double newVal = 0;
				for(int k = 0; k < order - derivative + 1; k++) {
					newVal += uStart[i][k] * startStopWeights[k][j];
				}
				values[j] = newVal;
			}
			//
			double newTic = 0;
			for(int j = 0; j < width; j++) {
				newTic += ticValues[j] * values[j];
			}
			newTicValues[i] = newTic;
		}
	}

	private static void processMiddle(int p, double[] ticValues, double[] newTicValues, double[] middleWeights, double[] coefficients) {

		for(int i = p; i < ticValues.length - p; i++) {
			double newTic = 0;
			for(int j = -p, k = 0; j <= p; j++, k++) {
				double ticValue = ticValues[i + j];
				double sgValue = middleWeights[k];
				newTic += (ticValue * sgValue) * coefficients[0];
			}
			newTicValues[i] = newTic;
		}
	}

	private static void processEnd(int p, int order, int derivative, int width, RealMatrix x, double[] ticValues, double[] newTicValues, double[][] startStopWeights) {

		double[][] uEnd = new double[p][order - derivative + 1]; // rows, columns
		for(int i = p + 1, k = 0; i < x.getRowDimension(); i++, k++) {
			for(int j = 0; j < order - derivative + 1; j++) {
				uEnd[k][j] = x.getEntry(i, j);
			}
		}
		//
		for(int i = ticValues.length - p, m = 0; i < ticValues.length; i++, m++) {
			double[] values = new double[width];
			for(int j = 0; j < width; j++) {
				double newVal = 0;
				for(int k = 0; k < order - derivative + 1; k++) {
					newVal += uEnd[m][k] * startStopWeights[k][j];
				}
				values[j] = newVal;
			}
			//
			double newTic = 0;
			for(int j = 0, k = ticValues.length - width; j < width; j++, k++) {
				newTic += ticValues[k] * values[j];
			}
			newTicValues[i] = newTic;
		}
	}

	private static double[][] getWeights(int width, int order, int derivative) {

		RealMatrix x = getX(width, order);
		RealMatrix dm = MatrixUtils.createRealIdentityMatrix(width);
		QRDecomposition qrDecomposition = new QRDecomposition(x);
		RealMatrix q = qrDecomposition.getQ();
		RealMatrix r2 = qrDecomposition.getR();
		SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(x);
		int r = singularValueDecomposition.getRank();
		/*
		 * getSubMatrix(startRow, endRow, startColumn, endColumn)
		 */
		RealMatrix q2 = q.getSubMatrix(0, q.getRowDimension() - 1, 0, r - 1);
		RealMatrix r3 = r2.getSubMatrix(0, r - 1, 0, r2.getColumnDimension() - 1);
		RealMatrix weights2 = new LUDecomposition(r3).getSolver().getInverse().multiply(q2.transpose().multiply(dm));
		//
		double[][] weights = new double[weights2.getRowDimension()][weights2.getColumnDimension()];
		for(int i = 0; i < weights2.getRowDimension(); i++) {
			for(int j = 0; j < weights2.getColumnDimension(); j++) {
				weights[i][j] = weights2.getEntry(i, j);
			}
		}
		//
		return weights;
	}

	private static RealMatrix getX(int width, int order) {

		int rows = width;
		int columns = 1 + order;
		int p = calculateP(width);
		double[][] t1 = createT1(rows, columns, -p);
		double[][] t2 = createT2(rows, columns, 0);
		return MatrixUtils.createRealMatrix(calculateX(t1, t2));
	}

	private static int calculateP(int width) {

		return (width - 1) / 2;
	}

	private static double[][] createT1(int rows, int columns, int min) {

		double[][] array = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				array[i][j] = min;
			}
			min++;
		}
		return array;
	}

	private static double[][] createT2(int rows, int columns, int min) {

		double[][] array = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			int value = min;
			for(int j = 0; j < columns; j++) {
				array[i][j] = value++;
			}
		}
		return array;
	}

	private static double[][] calculateX(double[][] t1, double[][] t2) {

		int rows = t1.length;
		int columns = t1[0].length;
		//
		double[][] array = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				array[i][j] = Math.pow(t1[i][j], t2[i][j]);
			}
		}
		return array;
	}

	private static double[] calculateCoefficient(int derivative, int order) {

		double[] result;
		if(derivative > 0) {
			/*
			 * Calculate the coefficient.
			 */
			int val1 = order + 1 - derivative;
			//
			double[][] t3 = createOnes(derivative, 1); // t3 one column
			double[] t4 = createArray(val1, 1);
			double[][] t34 = new double[t3.length][t4.length];
			for(int i = 0; i < t3.length; i++) {
				double valt3 = t3[i][0]; // t3 one column
				for(int j = 0; j < t4.length; j++) {
					t34[i][j] = valt3 * t4[j];
				}
			}
			//
			double[] t5 = createArray(derivative, 0);
			double[][] t6 = createOnes(1, val1); // t6 one row
			double[][] t56 = new double[t5.length][t6[0].length];
			for(int i = 0; i < t5.length; i++) {
				double valt5 = t5[i]; // t5 transpose
				for(int j = 0; j < t6[0].length; j++) { // t6 one row
					t56[i][j] = valt5 * t6[0][j]; // t6 one row
				}
			}
			//
			int size = t34[0].length; // size of the columns
			result = new double[size];
			int rows = t34.length;
			int columns = t34[0].length;
			for(int j = 0; j < columns; j++) {
				double product = 1;
				for(int i = 0; i < rows; i++) {
					product *= t34[i][j] + t56[i][j];
				}
				result[j] = product;
			}
			//
		} else {
			/*
			 * Set a coefficient default value.
			 */
			int size = order + 1;
			result = new double[size];
			for(int i = 0; i < size; i++) {
				result[i] = 1;
			}
		}
		return result;
	}

	private static double[][] createOnes(int rows, int columns) {

		double[][] array = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				array[i][j] = 1;
			}
		}
		return array;
	}

	private static double[] createArray(int size, int start) {

		double[] array = new double[size];
		int value = start;
		for(int i = 0; i < size; i++) {
			array[i] = value++;
		}
		return array;
	}

	public static IChromatogramFilterResult apply(ITotalScanSignals totalSignals, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		double[] sgTic = smooth(totalSignals, filterSettings, monitor);
		int i = 0;
		for(ITotalScanSignal signal : totalSignals.getTotalScanSignals()) {
			signal.setTotalSignal((float)sgTic[i++]);
		}
		return new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been applied successfully.");
	}

	public static void apply(double[][] matrix, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		double[] ionSignal = new double[matrix.length];
		for(int i = 0; i < matrix[0].length; i++) {
			for(int j = 0; j < matrix.length; j++) {
				ionSignal[j] = matrix[j][i];
			}
			ionSignal = smooth(ionSignal, filterSettings, monitor);
			for(int j = 0; j < matrix.length; j++) {
				if(ionSignal[j] < 0.0) {
					ionSignal[j] = 0.0;
				}
				matrix[j][i] = ionSignal[j];
			}
		}
	}
}
