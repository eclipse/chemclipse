/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add contentEquals / javadoc
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

public interface IRetentionTimeRange {

	/**
	 * @return the start retention time in milliseconds or -1 if the start is unknown
	 */
	int getStartRetentionTime();

	/**
	 * @return the stop retention time in milliseconds or -1 if the stop is unknown
	 */
	int getStopRetentionTime();

	/**
	 * Compares this objects content to the other objects content, the default implementation compares {@link #getStartRetentionTime()}, {@link #getStopRetentionTime()}
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare different instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean contentEquals(IRetentionTimeRange other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		return getStartRetentionTime() == other.getStartRetentionTime() && getStopRetentionTime() == other.getStopRetentionTime();
	}
}
