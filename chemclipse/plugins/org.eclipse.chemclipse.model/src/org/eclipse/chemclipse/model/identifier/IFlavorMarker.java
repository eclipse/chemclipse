/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.io.Serializable;
import java.util.Set;

public interface IFlavorMarker extends Serializable {

	void clear();

	boolean isManuallyVerified();

	void setManuallyVerified(boolean manuallyVerified);

	String getOdor();

	String getMatrix();

	String getSolvent();

	String getSamplePreparation();

	void setSamplePreparation(String samplePreparation);

	String getLiteratureReference();

	void setLiteratureReference(String literatureReference);

	/**
	 * Returns an unmodifiable set.
	 * 
	 * @return
	 */
	Set<IOdorThreshold> getOdorThresholds();

	void add(IOdorThreshold odorThreshold);

	void remove(IOdorThreshold odorThreshold);

	void clearOdorThresholds();
}