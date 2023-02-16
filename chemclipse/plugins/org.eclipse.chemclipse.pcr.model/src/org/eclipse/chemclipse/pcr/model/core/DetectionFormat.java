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
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;

public class DetectionFormat extends AbstractMeasurementInfo implements IDetectionFormat {

	private static final long serialVersionUID = -5323179455378035575L;
	private List<IChannelSpecification> channelSpecifications = new ArrayList<>();
	private List<Integer> emissionWavlengths = new ArrayList<>();
	private List<Integer> excitationWavlengths = new ArrayList<>();

	public DetectionFormat() {

		addProtectedKey(NAME);
	}

	@Override
	public String getName() {

		return getHeaderDataOrDefault(NAME, "");
	}

	@Override
	public List<IChannelSpecification> getChannelSpecifications() {

		return channelSpecifications;
	}

	@Override
	public List<Integer> getEmissionWavlengths() {

		return emissionWavlengths;
	}

	@Override
	public List<Integer> getExcitationWavlengths() {

		return excitationWavlengths;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		DetectionFormat other = (DetectionFormat)obj;
		if(getName() == null) {
			if(other.getName() != null) {
				return false;
			}
		} else if(!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		return "DetectionFormat [name=" + getName() + "]";
	}
}
