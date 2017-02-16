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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.SeriesData;

public class SeriesConverter {

	public static final String LINE_SERIES_1 = "LineSeries1";
	public static final String LINE_SERIES_2 = "LineSeries2";
	public static final String BAR_SERIES_1 = "BarSeries1";
	public static final String BAR_SERIES_2 = "BarSeries2";
	public static final String SCATTER_SERIES_1 = "ScatterSeries1";

	public static ISeriesData getSeries(String fileName) {

		ISeriesData seriesData = new SeriesData();
		//
		int size = getNumberOfLines(fileName);
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		BufferedReader bufferedReader = null;
		try {
			String line;
			int i = 0;
			bufferedReader = new BufferedReader(new InputStreamReader(SeriesConverter.class.getResourceAsStream(fileName)));
			while((line = bufferedReader.readLine()) != null) {
				String[] values = line.split("\t");
				xSeries[i] = Double.parseDouble(values[0].trim());
				ySeries[i] = Double.parseDouble(values[1].trim());
				i++;
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
		seriesData.setXSeries(xSeries);
		seriesData.setYSeries(ySeries);
		return seriesData;
	}

	public static List<ISeriesData> getScatterSeries(String fileName) {

		List<ISeriesData> scatterSeriesList = new ArrayList<ISeriesData>();
		//
		BufferedReader bufferedReader = null;
		try {
			String line;
			bufferedReader = new BufferedReader(new InputStreamReader(SeriesConverter.class.getResourceAsStream(fileName)));
			while((line = bufferedReader.readLine()) != null) {
				String[] values = line.split("\t");
				ISeriesData seriesData = new SeriesData();
				seriesData.setId(values[0].trim());
				seriesData.setXSeries(new double[]{Double.parseDouble(values[1].trim())});
				seriesData.setYSeries(new double[]{Double.parseDouble(values[2].trim())});
				scatterSeriesList.add(seriesData);
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
		return scatterSeriesList;
	}

	private static int getNumberOfLines(String fileName) {

		int i = 0;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(SeriesConverter.class.getResourceAsStream(fileName)));
			while((bufferedReader.readLine()) != null) {
				i++;
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
		return i;
	}
}
