/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add color compensation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.model.core.AbstractMeasurement;

public class Well extends AbstractMeasurement implements IWell {

	private static final long serialVersionUID = -5674593451119855940L;
	//
	private static final String ALL_SUBSETS_SELECTED = "";
	//
	private IChannel activeChannel = null;
	private String activeSubset = ALL_SUBSETS_SELECTED;
	private Position position = new Position();
	private Map<Integer, IChannel> channels = new HashMap<>();

	public Well() {

		addProtectedKey(SAMPLE_ID);
		addProtectedKey(TARGET_NAME);
		addProtectedKey(SAMPLE_SUBSET);
		addProtectedKey(SAMPLE_TYPE);
	}

	@Override
	public String getLabel() {

		if(isEmptyMeasurement()) {
			return getPosition().toString();
		} else {
			return getPosition().toString() + ": " + getSampleId();
		}
	}

	@Override
	public void setPosition(Position position) {

		this.position = position;
	}

	@Override
	public IChannel getActiveChannel() {

		return activeChannel;
	}

	@Override
	public void setActiveChannel(int activeChannel) {

		if(channels.keySet().contains(activeChannel)) {
			this.activeChannel = channels.get(activeChannel);
		}
	}

	@Override
	public void clearActiveChannel() {

		this.activeChannel = null;
	}

	@Override
	public boolean isActiveSubset() {

		if(ALL_SUBSETS_SELECTED.equals(activeSubset)) {
			return true;
		} else {
			return activeSubset.equals(getSampleSubset());
		}
	}

	@Override
	public void setActiveSubset(String activeSubset) {

		this.activeSubset = activeSubset;
	}

	@Override
	public void clearActiveSubset() {

		this.activeSubset = ALL_SUBSETS_SELECTED;
	}

	@Override
	public Position getPosition() {

		return position;
	}

	@Override
	public Map<Integer, IChannel> getChannels() {

		return channels;
	}

	@Override
	public int compareTo(IWell well) {

		if(well != null) {
			return Integer.compare(position.getId(), well.getPosition().getId());
		} else {
			return 0;
		}
	}

	@Override
	public String getSampleId() {

		return getHeaderDataOrDefault(SAMPLE_ID, "").trim();
	}

	@Override
	public String getSampleSubset() {

		return getHeaderDataOrDefault(SAMPLE_SUBSET, "").trim();
	}

	@Override
	public SampleType getSampleType() {

		String sampleType = getHeaderDataOrDefault(SAMPLE_TYPE, "").trim();
		if(sampleType.isEmpty() || sampleType.isBlank()) {
			return SampleType.UNKNOWN;
		}
		return SampleType.valueOf(sampleType);
	}

	@Override
	public void setSampleType(SampleType sampleType) {

		putHeaderData(SAMPLE_TYPE, getSampleType().toString());
	}

	@Override
	public String getTargetName() {

		return getHeaderDataOrDefault(TARGET_NAME, "").trim();
	}

	@Override
	public TargetType getTargetType() {

		String targetType = getHeaderDataOrDefault(TARGET_TYPE, "").trim();
		if(targetType.isEmpty() || targetType.isBlank()) {
			return TargetType.TARGET_OF_INTEREST;
		}
		return TargetType.valueOf(targetType);
	}

	@Override
	public void setTargetType(TargetType targetType) {

		putHeaderData(TARGET_TYPE, getTargetType().toString());
	}

	@Override
	public boolean isEmptyMeasurement() {

		return ("".equals(getSampleId().trim()) || "_".equals(getSampleId().trim()));
	}

	@Override
	public boolean isPositiveMeasurement() {

		if(activeChannel == null) {
			/*
			 * All channels
			 */
			for(IChannel channel : channels.values()) {
				if(isChannelPositive(channel)) {
					return true;
				}
			}
		} else {
			/*
			 * Selected channel
			 */
			return isChannelPositive(activeChannel);
		}
		return false;
	}

	@Override
	public void applyDetectionFormat(IDetectionFormat detectionFormat) {

		List<Integer> keys = new ArrayList<>(channels.keySet());
		Collections.sort(keys);
		//
		for(Integer key : keys) {
			/*
			 * Default detection Name
			 */
			IChannel channel = channels.get(key);
			String detectionName = IChannel.CHANNEL + " " + key; // Default
			//
			if(detectionFormat != null) {
				if(key >= 0 && key < detectionFormat.getChannelSpecifications().size()) {
					IChannelSpecification channelSpecification = detectionFormat.getChannelSpecifications().get(key);
					detectionName = channelSpecification.getName();
				}
			}
			//
			channel.setDetectionName(detectionName);
		}
	}

	private boolean isChannelPositive(IChannel channel) {

		return (channel != null && channel.getCrossingPoint() > 0.0d);
	}

	@Override
	public IWell makeDeepCopy() {

		IWell well = new Well();
		well.getPosition().setId(position.getId());
		well.getPosition().setColumn(position.getColumn());
		well.getPosition().setRow(position.getRow());
		for(Entry<Integer, IChannel> set : channels.entrySet()) {
			well.getChannels().put(set.getKey(), set.getValue().makeDeepCopy());
		}
		well.putHeaderData(SAMPLE_ID, getSampleId());
		well.putHeaderData(TARGET_NAME, getTargetName());
		well.putHeaderData(SAMPLE_SUBSET, getSampleSubset());
		well.putHeaderData(SAMPLE_TYPE, getSampleType().toString());
		return well;
	}
}
