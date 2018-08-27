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

import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;

public class Plate extends AbstractMeasurementInfo implements IPlate {

	private static final long serialVersionUID = -7209280707411376156L;
	private TreeSet<IWell> wells = new TreeSet<IWell>();

	@Override
	public TreeSet<IWell> getWells() {

		return wells;
	}

	@Override
	public IWell getWell(int id) {

		for(IWell well : wells) {
			if(well.getPosition().getId() == id) {
				return well;
			}
		}
		return null;
	}
}
