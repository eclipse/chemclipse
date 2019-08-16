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
package org.eclipse.chemclipse.numeric.internal.gaussjordan;

import static java.lang.Math.abs;

import org.eclipse.chemclipse.numeric.exceptions.GaussJordanError;

public class GaussJordan {

	/**
	 * Returns the matrix product of the matrix a transposed multiplied by a
	 * (At*A).
	 * 
	 * @param a
	 * @return double[][]
	 */
	public double[][] AtA(double[][] a) {

		/*
		 * double[][] a = {{2,5}, {7,3}, {6,4}, {2,5}}; -----------> n | 2 5 | 7
		 * 3 | 6 4 | 2 5 v m 2 7 6 2 2 5 5 3 4 5 7 3 6 4 2 5 see matrix
		 * operations: http://de.wikipedia.org/wiki/Matrix_(Mathematik)
		 * double[][] ata = {{93,65}, {65,75}};
		 */
		int m = a.length;
		int n = a[0].length;
		double[][] ata = new double[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				for(int k = 0; k < n; k++) {
					ata[i][k] += a[j][i] * a[j][k];
				}
			}
		}
		return ata;
	}

	/**
	 * Returns the matrix a transposed and multiplied with b.
	 * 
	 * @param a
	 * @param b
	 * @return double[][]
	 */
	public double[][] AtB(double[][] a, double[][] b) {

		/*
		 * double[][] a = {{2,5}, {7,3}, {6,4}, {2,5}}; double[][] b = {{5,0},
		 * {4,0}, {3,0}, {5,0}}; -----------> n | 2 5 | 7 3 | 6 4 | 2 5 v m 2 7
		 * 6 2 5 5 3 4 5 5 3 5 see matrix operations:
		 * http://de.wikipedia.org/wiki/Matrix_(Mathematik) double[][] atb =
		 * {{66,0}, {74,0}};
		 */
		int m = a[0].length;
		int n = a.length;
		double[][] atb = new double[m][m];
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				atb[i][0] += a[j][i] * b[j][0];
			}
		}
		return atb;
	}

	/**
	 * See: Numerical Recipes in C++. The Art of Scientific Computing.: The Art
	 * of Scientific Computing (Gebundene Ausgabe) Authors: William H. Press,
	 * Saul A. Teukolsky, William T. Vettering, Brian P. Flannery ISBN:
	 * 0521750334
	 * 
	 * @param a
	 * @param b
	 * @throws GaussJordanError
	 */
	public double[] solve(double[][] a, double[][] b) throws GaussJordanError {

		int i, j, k, l, ll;
		int icol = 0;
		int irow = 0;
		double big, dum, pivinv;
		int n = a.length;
		int m = b.length;
		int[] ipiv = new int[n];
		int[] indxr = new int[n];
		int[] indxc = new int[n];
		for(i = 0; i < n; i++) {
			big = 0.0;
			for(j = 0; j < n; j++) {
				if(ipiv[j] != 1) {
					for(k = 0; k < n; k++) {
						if(ipiv[k] == 0) {
							if(abs(a[j][k]) >= big) {
								big = abs(a[j][k]);
								irow = j;
								icol = k;
							}
						}
					}
				}
			}
			++(ipiv[icol]);
			if(irow != icol) {
				for(l = 0; l < n; l++) {
					swap(a, irow, icol, l);
				}
				for(l = 0; l < m; l++) {
					swap(b, irow, icol, l);
				}
			}
			indxr[i] = irow;
			indxc[i] = icol;
			if(a[icol][icol] == 0.0) {
				throw new GaussJordanError("gaussj: Singular Matrix");
			}
			pivinv = 1.0 / a[icol][icol];
			a[icol][icol] = 1.0;
			for(l = 0; l < n; l++) {
				a[icol][l] *= pivinv;
			}
			for(l = 0; l < m; l++) {
				b[icol][l] *= pivinv;
			}
			for(ll = 0; ll < n; ll++) {
				if(ll != icol) {
					dum = a[ll][icol];
					a[ll][icol] = 0.0;
					for(l = 0; l < n; l++) {
						a[ll][l] -= a[icol][l] * dum;
					}
					for(l = 0; l < m; l++) {
						b[ll][l] -= b[icol][l] * dum;
					}
				}
			}
		}
		for(l = n - 1; l >= 0; l--) {
			if(indxr[l] != indxc[l]) {
				for(k = 0; k < n; k++) {
					swapX(a, k, indxr[l], indxc[l]);
				}
			}
		}
		// das Ergebnis als eindimensionalen Vektor zurückgeben?
		double[] result = new double[m];
		for(i = 0; i < m; i++) {
			result[i] = b[i][0];
		}
		return result;
	}

	// TODO vereinheitlichen, gehört zu solve
	private void swap(double[][] array, int irow, int icol, int l) {

		double tmp = array[irow][l];
		array[irow][l] = array[icol][l];
		array[icol][l] = tmp;
	}

	// TODO vereinheitlichen, gehört zu solve
	private void swapX(double[][] array, int k, int indxr, int indxc) {

		double tmp = array[k][indxr];
		array[k][indxr] = array[k][indxc];
		array[k][indxc] = tmp;
	}
}
