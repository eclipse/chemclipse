/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SeriesConverter {

	public static double[] getXSeries() {

		return getSeries("xSeries", 30799);
	}

	public static double[] getYSeries() {

		return getSeries("ySeries", 30799);
	}

	private static double[] getSeries(String fileName, int size) {

		double[] series = new double[size];
		BufferedReader bufferedReader = null;
		try {
			String line;
			int i = 0;
			bufferedReader = new BufferedReader(new InputStreamReader(SeriesConverter.class.getResourceAsStream(fileName)));
			while((line = bufferedReader.readLine()) != null) {
				series[i++] = Double.parseDouble(line.trim());
			}
		} catch(Exception e) {
			//
		} finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch(IOException e) {
					//
				}
			}
		}
		return series;
	}
}
