/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.Map;

public interface IWell extends IScanPCR, Comparable<IWell> {

	Position getPosition();

	String getSampleId();

	void setSampleId(String sampleId);

	String getStatus();

	void setStatus(String status);

	String getResult();

	void setResult(String result);

	String getInterpretation();

	void setInterpretation(String interpretation);

	Map<Integer, IChannel> getChannels();

	int getCall();

	void setCall(int call);

	int getChannel();

	void setChannel(int channel);

	boolean isIncluded();

	void setIncluded(boolean isIncluded);

	String getTargetName();

	void setTargetName(String targetName);

	String getWarnDesc();

	void setWarnDesc(String warnDesc);

	String getWarnCodes();

	void setWarnCodes(String warnCodes);

	double getMaxFluor();

	void setMaxFluor(double maxFluor);

	double getMaxFluorBack();

	void setMaxFluorBack(double maxFluorBack);

	double getCrossingPoint();

	void setCrossingPoint(double crossingPoint);

	void setPosition(Position position);
}
