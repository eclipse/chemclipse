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

import java.util.HashMap;
import java.util.Map;

public class Well extends ScanPCR implements IWell {

	private static final long serialVersionUID = -9183326941662161376L;
	//
	private Position position = new Position();
	//
	private String sampleId = ""; // M-17-19288-011
	private String status = "";
	private String result = "";
	private String interpretation = "";
	private Map<Integer, IChannel> channels = new HashMap<>();
	//
	private int call = 0; // 7
	private int channel; // 1
	private boolean isIncluded = false;
	private String targetName = ""; // SLM
	private String warnDesc = ""; // Control failure: Positive Control qcsInternal control failed in position A1
	private String warnCodes = ""; // C
	private double maxFluor = 0.0d; // 263.825689861719
	private double maxFluorBack = 0.0d; // 89.3118462487466
	private double crossingPoint = 0.0d; // NAN

	@Override
	public Position getPosition() {

		return position;
	}

	@Override
	public String getSampleId() {

		return sampleId;
	}

	@Override
	public void setSampleId(String sampleId) {

		this.sampleId = sampleId;
	}

	@Override
	public String getStatus() {

		return status;
	}

	@Override
	public void setStatus(String status) {

		this.status = status;
	}

	@Override
	public String getResult() {

		return result;
	}

	@Override
	public void setResult(String result) {

		this.result = result;
	}

	@Override
	public String getInterpretation() {

		return interpretation;
	}

	@Override
	public void setInterpretation(String interpretation) {

		this.interpretation = interpretation;
	}

	@Override
	public Map<Integer, IChannel> getChannels() {

		return channels;
	}

	@Override
	public int getCall() {

		return call;
	}

	@Override
	public void setCall(int call) {

		this.call = call;
	}

	@Override
	public int getChannel() {

		return channel;
	}

	@Override
	public void setChannel(int channel) {

		this.channel = channel;
	}

	@Override
	public boolean isIncluded() {

		return isIncluded;
	}

	@Override
	public void setIncluded(boolean isIncluded) {

		this.isIncluded = isIncluded;
	}

	@Override
	public String getTargetName() {

		return targetName;
	}

	@Override
	public void setTargetName(String targetName) {

		this.targetName = targetName;
	}

	@Override
	public String getWarnDesc() {

		return warnDesc;
	}

	@Override
	public void setWarnDesc(String warnDesc) {

		this.warnDesc = warnDesc;
	}

	@Override
	public String getWarnCodes() {

		return warnCodes;
	}

	@Override
	public void setWarnCodes(String warnCodes) {

		this.warnCodes = warnCodes;
	}

	@Override
	public double getMaxFluor() {

		return maxFluor;
	}

	@Override
	public void setMaxFluor(double maxFluor) {

		this.maxFluor = maxFluor;
	}

	@Override
	public double getMaxFluorBack() {

		return maxFluorBack;
	}

	@Override
	public void setMaxFluorBack(double maxFluorBack) {

		this.maxFluorBack = maxFluorBack;
	}

	@Override
	public double getCrossingPoint() {

		return crossingPoint;
	}

	@Override
	public void setCrossingPoint(double crossingPoint) {

		this.crossingPoint = crossingPoint;
	}

	@Override
	public void setPosition(Position position) {

		this.position = position;
	}

	@Override
	public int compareTo(IWell well) {

		if(well != null) {
			return Integer.compare(position.getId(), well.getPosition().getId());
		} else {
			return 0;
		}
	}
}
