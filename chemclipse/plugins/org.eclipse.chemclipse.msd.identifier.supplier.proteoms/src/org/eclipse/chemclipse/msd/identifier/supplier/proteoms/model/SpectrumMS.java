/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model;

import java.util.ArrayList;
import java.util.List;

public class SpectrumMS extends AbstractSpectrum {

	public static final int version = 1;
	private List<SpectrumMSMS> msmsSpectrumsChildren = null;

	public SpectrumMS() {
	}

	public SpectrumMS(long id, String name) {
		super(id, name);
	}

	@Override
	public String toString() {

		return "MS id=" + getId() + " name='" + getName() + "', msms count='" + getMsmsSpectrumsChildren().size() + "'";
	}

	public void addMSMSchild(SpectrumMSMS msms) {

		getMsmsSpectrumsChildren().add(msms);
		msms.setParentMS(this);
	}

	public List<SpectrumMSMS> getMsmsSpectrumsChildren() {

		if(msmsSpectrumsChildren == null) {
			msmsSpectrumsChildren = new ArrayList<>();
		}
		return msmsSpectrumsChildren;
	}

	public void setMsmsSpectrumsChildren(List<SpectrumMSMS> msmsSpectrumsChildren) {

		this.msmsSpectrumsChildren = msmsSpectrumsChildren;
		if(msmsSpectrumsChildren != null) {
			for(SpectrumMSMS msms : msmsSpectrumsChildren) {
				msms.setParentMS(this);
			}
		}
	}

	public int getNumberOfMSMS() {

		if(msmsSpectrumsChildren == null) {
			return 0;
		}
		return msmsSpectrumsChildren.size();
	}
}
