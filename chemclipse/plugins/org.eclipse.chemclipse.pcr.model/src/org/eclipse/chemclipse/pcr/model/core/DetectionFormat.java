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

import java.util.ArrayList;
import java.util.List;

public class DetectionFormat extends AbstractDataModel implements IDetectionFormat {

	private List<IChannelSpecification> channelSpecifications = new ArrayList<>();
	private List<Integer> emissionWavlengths = new ArrayList<>();
	private List<Integer> excitationWavlengths = new ArrayList<>();

	public DetectionFormat() {
		addProtectedKey(NAME);
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
		result = prime * result + ((getData(NAME, "") == null) ? 0 : getData(NAME, "").hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		DetectionFormat other = (DetectionFormat)obj;
		if(getData(NAME, "") == null) {
			if(other.getData(NAME, "") != null)
				return false;
		} else if(!getData(NAME, "").equals(other.getData(NAME, "")))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "DetectionFormat [name=" + getData(NAME, "") + "]";
	}
}
