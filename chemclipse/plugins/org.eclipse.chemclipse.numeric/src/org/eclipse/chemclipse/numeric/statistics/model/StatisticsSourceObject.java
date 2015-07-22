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

public class StatisticsSourceObject<T> implements IStatisticsSourceObject<T> {

	private boolean included;
	private final T sourceObject;

	public StatisticsSourceObject(boolean included, T sourceObject) {

		this.included = included;
		this.sourceObject = sourceObject;
	}

	@Override
	public void setIncluded(boolean included) {

		this.included = included;
	}

	@Override
	public boolean isIncluded() {

		return included;
	}

	@Override
	public T getSourceObject() {

		return sourceObject;
	}
}
