/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.List;

public class PeakQuantitations {

	private List<String> titles = new ArrayList<String>();
	private List<PeakQuantitation> quantitationEntries = new ArrayList<PeakQuantitation>();

	public List<String> getTitles() {

		return titles;
	}

	public List<PeakQuantitation> getQuantitationEntries() {

		return quantitationEntries;
	}
}
