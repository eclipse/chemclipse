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
package org.eclipse.chemclipse.numeric.statistics;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Arrays;
import java.util.List;

public class Calculations {

	/**
	 * Returns the sum of the given array.
	 * 
	 * @param values
	 * @return
	 */
	public static int getSum(int[] values) {

		int sum = 0;
		for(int value : values) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Returns the sum of the given array.
	 * 
	 * @param values
	 * @return
	 */
	public static float getSum(float[] values) {

		float sum = 0.0f;
		for(float value : values) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Returns the sum of the given array.
	 * 
	 * @param values
	 * @return
	 */
	public static double getSum(double[] values) {

		double sum = 0.0d;
		for(double value : values) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Returns the euclidian length of the given array.<br/>
	 * Each value will be squared, then added and afterward the square root will
	 * be given back. (x21 + x22 + · · · + x2n)^1/2
	 * 
	 * @param values
	 * @return
	 */
	public static double getEuclidianLength(double[] values) {

		double sum = 0.0d;
		for(double value : values) {
			sum += Math.pow(value, 2);
		}
		return Math.sqrt(sum);
	}

	/**
	 * Returns the minimum value of the given array.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return double
	 */
	public static double getMin(double[] values) {

		if(values.length == 0) {
			return 0.0d;
		} else {
			/*
			 * @patch Witold Eryk Wolski
			 * @since 2012-09-17
			 */
			double minValue = values[0];
			for(int i = 1; i < values.length; i++) {
				if(values[i] < minValue) {
					minValue = values[i];
				}
			}
			return minValue;
		}
	}

	/**
	 * Returns the maximum value of the given array.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return double
	 */
	public static double getMax(double[] values) {

		if(values.length == 0) {
			return 0.0d;
		} else {
			double[] val = Arrays.copyOf(values, values.length);
			Arrays.sort(val);
			return val[val.length - 1];
		}
	}

	/**
	 * Returns the minimum value of the given array.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return float
	 */
	public static float getMin(float[] values) {

		if(values.length == 0) {
			return 0.0f;
		} else {
			float[] val = Arrays.copyOf(values, values.length);
			Arrays.sort(val);
			return val[0];
		}
	}

	/**
	 * Returns the maximum value of the given array.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return float
	 */
	public static float getMax(float[] values) {

		if(values.length == 0) {
			return 0.0f;
		} else {
			float[] val = Arrays.copyOf(values, values.length);
			Arrays.sort(val);
			return val[val.length - 1];
		}
	}

	/**
	 * Returns the mean value of an array of double values.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return double
	 */
	public static double getMean(double[] values) {

		double sum = 0.0;
		/*
		 * Check if the size is 0.
		 */
		int size = getSize(values);
		if(size == 0) {
			return 0.0;
		}
		/*
		 * Calculate the sum of all values.
		 */
		for(double value : values) {
			sum += value;
		}
		return sum / size;
	}

	/**
	 * Returns the mean value of an array of float values.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return float
	 */
	public static float getMean(float[] values) {

		float sum = 0.0f;
		/*
		 * Check if the size is 0.
		 */
		int size = getSize(values);
		if(size == 0) {
			return 0.0f;
		}
		/*
		 * Calculate the sum of all values.
		 */
		for(float value : values) {
			sum += value;
		}
		return sum / size;
	}

	/**
	 * Returns the mean value of an array of int values.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return float
	 */
	public static int getMean(int[] values) {

		int sum = 0;
		/*
		 * Check if the size is 0.
		 */
		int size = getSize(values);
		if(size == 0) {
			return 0;
		}
		/*
		 * Calculate the sum of all values.
		 */
		for(int value : values) {
			sum += value;
		}
		return sum / size;
	}

	/**
	 * Returns the median of the given value list.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return double
	 */
	public static double getMedian(List<Double> values) {

		double median = 0.0d;
		if(values != null) {
			double[] data = new double[values.size()];
			for(int i = 0; i < values.size(); i++) {
				if(values.get(i) != null) {
					data[i] = values.get(i);
				}
			}
			median = getMedian(data);
		}
		return median;
	}

	/**
	 * Returns the median of the given value array.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return
	 */
	public static double getMedian(double[] values) {

		double mean = 0.0d;
		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 3) {
			return 0.0f;
		}
		/*
		 * Take a look if the size of the array is odd or not.<br/> It is very
		 * important, that the list is sorted.
		 */
		double[] val = Arrays.copyOf(values, values.length);
		Arrays.sort(val);
		if(isOdd(size)) {
			int n1 = size / 2;
			mean = val[n1];
		} else {
			int n1 = (size - 1) / 2;
			int n2 = size / 2;
			mean = 0.5d * (val[n1] + val[n2]);
		}
		return mean;
	}

	/**
	 * Returns the median of the float array.<br/>
	 * It must contain at least 3 elements.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return
	 */
	public static float getMedian(float[] values) {

		float mean = 0.0f;
		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 3) {
			return 0.0f;
		}
		/*
		 * Take a look if the size of the array is odd or not.<br/> It is very
		 * important, that the list is sorted.
		 */
		float[] val = Arrays.copyOf(values, values.length);
		Arrays.sort(val);
		if(isOdd(size)) {
			int n1 = size / 2;
			mean = val[n1];
		} else {
			int n1 = (size - 1) / 2;
			int n2 = size / 2;
			mean = 0.5f * (val[n1] + val[n2]);
		}
		return mean;
	}

	/**
	 * Returns the median of the float array.<br/>
	 * It must contain at least 3 elements.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return
	 */
	public static int getMedian(int[] values) {

		int mean = 0;
		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 3) {
			return 0;
		}
		/*
		 * Take a look if the size of the array is odd or not.<br/> It is very
		 * important, that the list is sorted.
		 */
		int[] val = Arrays.copyOf(values, values.length);
		Arrays.sort(val);
		if(isOdd(size)) {
			int n1 = size / 2;
			mean = val[n1];
		} else {
			int n1 = (size - 1) / 2;
			int n2 = size / 2;
			mean = (int)(0.5f * (val[n1] + val[n2]));
		}
		return mean;
	}

	/**
	 * Calculates the median from the squareroot((value[i] - mean)^2) values.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return
	 */
	public static double getMedianDeviationFromMean(double[] values) {

		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 3) {
			return 0.0f;
		}
		double[] deviationFromMean = new double[values.length];
		double mean = getMean(values);
		for(int i = 0; i < values.length; i++) {
			deviationFromMean[i] = Math.sqrt(Math.pow((values[i] - mean), 2));
		}
		return getMedian(deviationFromMean);
	}

	/**
	 * Calculates the median from the squareroot((value[i] - median)^2) values.<br/>
	 * Example for use in AMDIS ms detector, it is not clear if from mean or
	 * median was thought to be calculated.<br/>
	 * Use of the value "mean" is more robust so it is used here.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return
	 */
	public static double getMedianDeviationFromMedian(double[] values) {

		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 3) {
			return 0.0f;
		}
		double[] deviationFromMedian = new double[values.length];
		double median = getMedian(values);
		for(int i = 0; i < values.length; i++) {
			deviationFromMedian[i] = Math.sqrt(Math.pow((values[i] - median), 2));
		}
		return getMedian(deviationFromMedian);
	}

	/**
	 * Returns the variance.<br/>
	 * The size of the value array must be greater or equal than 2.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return double
	 */
	public static double getVariance(double[] values) {

		double mean = getMean(values);
		return getVariance(values, mean);
	}

	/**
	 * Returns the variance.<br/>
	 * The size of the value array must be greater or equal than 2.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return float
	 */
	public static float getVariance(float[] values) {

		float mean = getMean(values);
		return getVariance(values, mean);
	}

	/**
	 * Returns the variance.<br/>
	 * The size of the value array must be greater or equal than 2.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @param mean
	 * @return double
	 */
	public static double getVariance(double[] values, double mean) {

		double sum = 0.0;
		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 2) {
			return 0.0;
		}
		for(double value : values) {
			sum += pow((value - mean), 2);
		}
		return (1.0d / (size - 1)) * sum;
	}

	/**
	 * Returns the variance.<br/>
	 * The size of the value array must be greater or equal than 2.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @param mean
	 * @return float
	 */
	public static float getVariance(float[] values, float mean) {

		float sum = 0.0f;
		/*
		 * Check if the size is lower than 2.
		 */
		int size = getSize(values);
		if(size < 2) {
			return 0.0f;
		}
		for(float value : values) {
			sum += (float)(pow((value - mean), 2));
		}
		return (1.0f / (size - 1)) / sum;
	}

	/**
	 * Returns the standard deviation.
	 * 
	 * @param medianFromMean
	 * @return double
	 */
	public static double getStandardDeviation(double medianFromMean) {

		return sqrt(medianFromMean);
	}

	/**
	 * Returns the standard deviation.
	 * 
	 * @param medianFromMean
	 * @return float
	 */
	public static float getStandardDeviation(float medianFromMean) {

		return (float)sqrt(medianFromMean);
	}

	/**
	 * Returns the standard deviation.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return double
	 */
	public static double getStandardDeviation(double[] values) {

		double variance = getVariance(values);
		return sqrt(variance);
	}

	/**
	 * Returns the standard deviation.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @return float
	 */
	public static float getStandardDeviation(float[] values) {

		float median = getVariance(values);
		return (float)sqrt(median);
	}

	/**
	 * Returns the standard deviation.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @param mean
	 * @return double
	 */
	public static double getStandardDeviation(double[] values, double mean) {

		double median = getVariance(values, mean);
		return sqrt(median);
	}

	/**
	 * Returns the standard deviation.<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 * @param mean
	 * @return float
	 */
	public static float getStandardDeviation(float[] values, float mean) {

		float median = getVariance(values, mean);
		return (float)sqrt(median);
	}

	/**
	 * Normalizes the values in the given array to the highest value.<br/>
	 * max - is the highest value of the array.<br/>
	 * Only positive values are allowed. If max is 0 or the array contains no
	 * values, nothing will be done.<br/>
	 * Then each value will be calculates as:<br/>
	 * values[i] /= max;<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 */
	public static void normalize(float[] values) {

		/*
		 * The getMax(values) method takes care that the values will not be
		 * changed in order. Arrays.copyOf(...)
		 */
		float max = getMax(values);
		if(max > 0.0f) {
			for(int i = 0; i < values.length; i++) {
				values[i] /= max;
			}
		}
	}

	/**
	 * Normalizes the values in the given array to the highest value.<br/>
	 * max - is the highest value of the array.<br/>
	 * Only positive values are allowed. If max is 0 or the array contains no
	 * values, nothing will be done.<br/>
	 * Then each value will be calculates as:<br/>
	 * values[i] /= max;<br/>
	 * The values will not be changed in order.
	 * 
	 * @param values
	 */
	public static void normalize(double[] values) {

		/*
		 * The getMax(values) method takes care that the values will not be
		 * changed in order. Arrays.copyOf(...)
		 */
		double max = getMax(values);
		if(max > 0.0f) {
			for(int i = 0; i < values.length; i++) {
				values[i] /= max;
			}
		}
	}

	/**
	 * Smoothes the given values with a given window size.<br/>
	 * The smoothing will be done consecutively.<br/>
	 * Equations in:
	 * "A noise and background reduction method for component detection in liquid chromatography mass spectrometry"
	 * , Windig, W. and Phalp, J. M. and Payne, A. W., 1996<br/>
	 * See Equations: #7,8
	 * 
	 * @param values
	 * @param windowSize
	 */
	public static void smooth(double[] values, WindowSize windowSize) {

		int window = 1;
		/*
		 * Check the window size.
		 */
		if(windowSize != null && windowSize.getSize() > 0 && windowSize.getSize() <= values.length) {
			window = windowSize.getSize();
		}
		int lastScan = getWindowReducedLength(values, window);
		double sumSignals;
		/*
		 * Smooth the values.
		 */
		for(int i = 0; i < lastScan; i++) {
			sumSignals = 0.0d;
			for(int j = 0; j < window; j++) {
				sumSignals += values[j + i];
			}
			values[i] = (1.0d / window) * sumSignals;
		}
	}

	/**
	 * Returns the reduced window length of the given array. The window must be
	 * >= 1 and <= values.length. If values == null => 1 will be returned.<br/>
	 * If window < 1 || window > values.length => values.length will be
	 * returned.<br/>
	 */
	public static int getWindowReducedLength(double[] values, int window) {

		if(values == null) {
			return 0;
		}
		if(window < 1 || window > values.length) {
			window = 1;
		}
		return values.length - window + 1;
	}

	/**
	 * Normalizes the given value array to euclidian length.<br/>
	 * Equations in:
	 * "A noise and background reduction method for component detection in liquid chromatography mass spectrometry"
	 * , Windig, W. and Phalp, J. M. and Payne, A. W., 1996<br/>
	 * See Equations: #1,2
	 * 
	 * @param values
	 */
	public static void scaleToEuclidianLength(double[] values) {

		double euclidianLength = Calculations.getEuclidianLength(values);
		/*
		 * Length scale the vector.
		 */
		for(int i = 0; i < values.length; i++) {
			values[i] = values[i] / euclidianLength;
		}
	}

	/**
	 * Normalizes the given value to standardized length.<br/>
	 * Equations in:
	 * "A noise and background reduction method for component detection in liquid chromatography mass spectrometry"
	 * , Windig, W. and Phalp, J. M. and Payne, A. W., 1996<br/>
	 * See Equations: #3,4,5
	 * 
	 * @param values
	 */
	public static void scaleToStandardizedLength(double[] values) {

		/*
		 * Standardize the vector.
		 */
		double mean = Calculations.getMean(values);
		double standardDeviation = Calculations.getStandardDeviation(values, mean);
		for(int i = 0; i < values.length; i++) {
			values[i] = (values[i] - mean) / standardDeviation;
		}
	}

	/**
	 * Scales the given array to normalized unity.<br/>
	 * The sum of all array values will be 1 after calculation.<br/>
	 * If the square root sum of the given values is 0, the function returns
	 * without modifications.
	 * 
	 * @param values
	 */
	public static void scaleToNormalizedUnity(double[] values) {

		double powTwoSum = calculateSumPow2(values);
		if(powTwoSum == 0) {
			return;
		}
		double adjustedValue;
		for(int i = 0; i < values.length; i++) {
			adjustedValue = Math.pow(values[i], 2.0d) / powTwoSum;
			values[i] = adjustedValue;
		}
	}

	/**
	 * Returns the sum of the values in the given array powered by 2.
	 * 
	 * @param values
	 * @return
	 */
	public static double calculateSumPow2(double[] values) {

		double powTwoSum = 0.0d;
		for(int i = 0; i < values.length; i++) {
			powTwoSum += Math.pow(values[i], 2.0d);
		}
		return powTwoSum;
	}

	/**
	 * Returns the size of the double array.<br/>
	 * If the array is null, 0 will be returned.
	 * 
	 * @param values
	 * @return int
	 */
	private static int getSize(double[] values) {

		/*
		 * Check if values is null.
		 */
		if(values == null) {
			return 0;
		}
		return values.length;
	}

	/**
	 * Returns the size of the double array.<br/>
	 * If the array is null, 0 will be returned.
	 * 
	 * @param values
	 * @return int
	 */
	private static int getSize(float[] values) {

		/*
		 * Check if values is null.
		 */
		if(values == null) {
			return 0;
		}
		return values.length;
	}

	/**
	 * Returns the size of the double array.<br/>
	 * If the array is null, 0 will be returned.
	 * 
	 * @param values
	 * @return int
	 */
	private static int getSize(int[] values) {

		/*
		 * Check if values is null.
		 */
		if(values == null) {
			return 0;
		}
		return values.length;
	}

	/**
	 * Checks whether the values are odd or even.
	 * 
	 * @return
	 */
	private static boolean isOdd(int size) {

		if(size % 2 != 0) {
			return true;
		} else {
			return false;
		}
	}
}
