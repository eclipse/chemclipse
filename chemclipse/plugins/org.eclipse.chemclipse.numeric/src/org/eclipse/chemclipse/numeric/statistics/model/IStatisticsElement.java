/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics.model;

import java.util.List;

public interface IStatisticsElement<T> {

	Object getIdentifier();

	void setIdentifier(Object identifier);

	List<IStatisticsSourceObject<T>> getSourceElements();

	List<T> getIncludedSourceElements();

	void setSourceElements(List<IStatisticsSourceObject<T>> sourceElements);

	boolean isContentStatistics();

	IStatistics getStatisticsContent();

	void setStatisticsContent(IStatistics content);

	<S> List<IStatisticsElement<S>> getStatisticsElements();

	<S> void setStatisticsElements(List<IStatisticsElement<S>> content);
}