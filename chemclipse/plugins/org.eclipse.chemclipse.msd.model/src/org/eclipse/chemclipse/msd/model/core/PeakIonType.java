/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * You can mark a peak ion.<br/>
 * AMDIS, for example, uses this feature to flag uncertain peaks.<br/>
 * <br/>
 * AMDIS Examples (you can find those values in the *.ELU and *.FIN files):<br/>
 * (22,1 L0.7)<br/>
 * (19,1 B0.2)<br/>
 * (41,1 N0.2)<br/>
 * <br/>
 * There maybe some more types to be added here.
 * 
 * @author eselmeister
 */
public enum PeakIonType {
	NO_TYPE("no type stored"), B("B"), L("L"), N("N");

	private String description;

	private PeakIonType(String description) {
		this.description = description;
	}

	/**
	 * Returns the description of the peak ion type.
	 * 
	 * @return String
	 */
	public String getDescription() {

		return this.description;
	}
}
