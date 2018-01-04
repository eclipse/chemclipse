/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.series;

public interface IChromatogramRange {

	void reset();

	boolean isValid();

	int getStartRetentionTime();

	void setStartRetentionTime(int startRetentionTime);

	int getStopRetentionTime();

	void setStopRetentionTime(int stopRetentionTime);

	float getStartAbundance();

	void setStartAbundance(float startAbundance);

	float getStopAbundance();

	void setStopAbundance(float stopAbundance);
}