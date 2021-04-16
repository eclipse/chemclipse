/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.stat.inference.OneWayAnova;

public class AnovaStatistics implements IAnovaStatistics {

	Collection<double[]> anovaInput;

	public <T> AnovaStatistics(IStatisticsElement<IStatisticsElement<T>> groupedStatisticsElement, Method getdata) {

		List<IStatisticsElement<T>> groupedStatisticsElements = groupedStatisticsElement.getIncludedSourceElements();
		this.anovaInput = new ArrayList<double[]>(groupedStatisticsElements.size());
		if(groupedStatisticsElements.size() > 1) {
			for(IStatisticsElement<T> gse : groupedStatisticsElements) {
				List<T> statisticsElements = gse.getIncludedSourceElements();
				int size = statisticsElements.size();
				if(size > 1) {
					double[] values = new double[size];
					for(int i = 0; i < size; i++) {
						T t = statisticsElements.get(i);
						try {
							Number data = (Number)getdata.invoke(t, new Object[0]);
							values[i] = data.doubleValue();
						} catch(IllegalAccessException e) {
							e.printStackTrace();
						} catch(IllegalArgumentException e) {
							e.printStackTrace();
						} catch(InvocationTargetException e) {
							e.printStackTrace();
							e.getTargetException().printStackTrace();
						}
					}
					anovaInput.add(values);
				}
			}
		}
	}

	@Override
	public double getPValue() {

		OneWayAnova anova = new OneWayAnova();
		double pvalue = anova.anovaPValue(anovaInput);
		return pvalue;
	}

	@Override
	public double getFValue() {

		OneWayAnova anova = new OneWayAnova();
		double fvalue = anova.anovaFValue(anovaInput);
		return fvalue;
	}
}
