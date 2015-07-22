/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
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

public class StatisticsElement<T> {

	private Object identifier;
	private List<IStatisticsSourceObject<T>> sourceElements;
	private Object content; // this is either an IStatistics object or a List<StatisticsElement>

	public StatisticsElement(Object identifier, List<IStatisticsSourceObject<T>> sourceElements) {

		this.identifier = identifier;
		this.sourceElements = sourceElements;
	}

	public StatisticsElement(Object identifier, List<IStatisticsSourceObject<T>> sourceElements, IStatistics statistics) {

		this(identifier, sourceElements);
		this.content = statistics;
	}

	public StatisticsElement(Object identifier, List<IStatisticsSourceObject<T>> sourceElements, List<StatisticsElement> elements) {

		this(identifier, sourceElements);
		this.content = elements;
	}

	public Object getIdentifier() {

		return identifier;
	}

	public void setIdentifier(Object identifier) {

		this.identifier = identifier;
	}

	public List<IStatisticsSourceObject<T>> getSourceElements() {

		return sourceElements;
	}

	public void setSourceElements(List<IStatisticsSourceObject<T>> sourceElements) {

		this.sourceElements = sourceElements;
	}

	public Object getContent() {

		return content;
	}

	public void setContent(Object content) {

		this.content = content;
	}
}
