/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

public interface IReportPeak {

	int getRetentionTime();

	void setRetentionTime(int retentionTime);

	int getStartRetentionTime();

	void setStartRetentionTime(int startRetentionTime);

	int getStopRetentionTime();

	void setStopRetentionTime(int stopRetentionTime);

	double getArea();

	void setArea(double area);

	String getSubstance();

	void setSubstance(String substance);

	String getUnits();

	void setUnits(String units);

	String toString();
}