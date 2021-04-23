/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings;

public interface IUnknownSettings {

	float getLimitMatchFactor();

	void setLimitMatchFactor(float limitMatchFactor);

	String getTargetName();

	void setTargetName(String targetName);

	float getMatchQuality();

	void setMatchQuality(float matchQuality);

	int getNumberOfTraces();

	boolean isIncludeIntensityPercent();

	void setIncludeIntensityPercent(boolean includeIntensityPercent);

	String getMarkerStart();

	void setMarkerStart(String markerStart);

	String getMarkerStop();

	void setMarkerStop(String markerStop);

	boolean isIncludeRetentionTime();

	void setIncludeRetentionTime(boolean includeRetentionTime);

	boolean isIncludeRetentionIndex();

	void setIncludeRetentionIndex(boolean includeRetentionIndex);
}