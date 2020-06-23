/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.filter;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayFilter;

import junit.framework.TestCase;

public class SavitzkyGolayFilter_1_Test extends TestCase {

	public void test1() {

		// Reference value for filter length = 5, filter order = 2, derivative = 0
		// Generated in R using signal::sgolay(2,5,0)[3,] and verified in tables
		double[] reference = new double[]{-0.08571429, 0.34285714, 0.48571429, 0.34285714, -0.08571429};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(2, 5, 0);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test2() {

		// Reference value for filter length = 5, filter order = 3, derivative = 0
		// Generated in R using signal::sgolay(3,5,0)[3,] and verified in tables
		double[] reference = new double[]{-0.08571429, 0.34285714, 0.48571429, 0.34285714, -0.08571429};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(3, 5, 0);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test3() {

		// Reference value for filter length = 7, filter order = 3, derivative = 0
		// Generated in R using signal::sgolay(3,7,0)[4,] and verified in tables
		double[] reference = new double[]{-0.09523810, 0.1428571, 0.28571429, 0.3333333, 0.28571429, 0.1428571, -0.09523810};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(3, 7, 0);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test4() {

		// Reference value for filter length = 7, filter order = 3, derivative = 0
		// Generated in R using signal::sgolay(4,7,0)[4,] and verified in tables
		double[] reference = new double[]{0.02164502, -0.12987013, 0.32467532, 0.56709957, 0.32467532, -0.12987013, 0.02164502};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(4, 7, 0);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test5() {

		// Reference value for filter length = 9, filter order = 5, derivative = 0
		// Generated in R using signal::sgolay(5,9,0)[5,] and verified in tables
		double[] reference = new double[]{0.034965035, -0.12820513, 0.06993007, 0.31468531, 0.41724942, 0.31468531, 0.06993007, -0.12820513, 0.034965035};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(5, 9, 0);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test6() {

		// Reference value for filter length = 5, filter order = 2, derivative = 1
		// Generated in R using signal::sgolay(2,5,1)[3,] and verified in tables
		double[] reference = new double[]{-0.20000000, -0.10000000, 8.384186e-17, 0.10000000, 0.20000000};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(2, 5, 1);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test7() {

		// Reference value for filter length = 5, filter order = 3, derivative = 1
		// Generated in R using signal::sgolay(3,5,1)[3,] and verified in tables
		double[] reference = new double[]{0.08333333, -0.66666667, 0.0, 0.66666667, -0.08333333};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(3, 5, 1);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test8() {

		// Reference value for filter length = 5, filter order = 3, derivative = 1
		// Generated in R using signal::sgolay(3,7,1)[4,] and verified in tables
		double[] reference = new double[]{0.087301587, -0.26587302, -0.23015873, -5.414016e-17, 0.23015873, 0.26587302, -0.087301587};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(3, 7, 1);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test9() {

		// Reference value for filter length = 5, filter order = 2, derivative = 2
		// Generated in R using signal::sgolay(2,5,2)[3,] and verified in tables
		double[] reference = new double[]{0.2857143, -0.1428571, -0.2857143, -0.1428571, 0.2857143};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(2, 5, 2);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test10() {

		// Reference value for filter length = 5, filter order = 4, derivative = 2
		// Generated in R using signal::sgolay(4,5,2)[3,] and verified in tables
		double[] reference = new double[]{-0.08333333, 1.3333333, -2.5, 1.3333333, -0.08333333};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(4, 5, 2);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}

	public void test11() {

		// Reference value for filter length = 5, filter order = 2, derivative = 2
		// Generated in R using signal::sgolay(4,7,2)[4,] and verified in tables
		double[] reference = new double[]{-0.09848485, 0.50757576, -0.14393939, -0.5303030, -0.14393939, 0.50757576, -0.09848485};
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(4, 7, 2);
		double[] filterCoefficients = filter.getFactorialAdjustedFilterCoefficients();
		assertArrayEquals(null, filterCoefficients, reference, 0.0001);
	}
}
