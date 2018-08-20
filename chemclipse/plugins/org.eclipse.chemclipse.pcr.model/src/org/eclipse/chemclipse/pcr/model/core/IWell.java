/*******************************************************************************
 * Copyright (c) 2018 pwenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * pwenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.Map;

import javax.swing.text.Position;

public interface IWell extends IScanPCR, Comparable<IWell> {

	int getId();

	void setId(int id);

	Position getPosition();

	void setPosition(Position position);

	String getSampleId();

	void setSampleId(String sampleId);

	String getStatus();

	void setStatus(String status);

	String getResult();

	void setResult(String result);

	String getInterpretation();

	void setInterpretation(String interpretation);

	Map<Integer, IChannel> getChannels();
}
