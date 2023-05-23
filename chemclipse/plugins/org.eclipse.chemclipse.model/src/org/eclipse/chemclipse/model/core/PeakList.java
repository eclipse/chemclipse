/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

/**
 * Interface that supplies a stream of {@link PeakPosition}s
 * 
 * @author Christoph Läubrich
 *
 */
public class PeakList implements IMeasurementResult<Iterable<PeakPosition>> {

	private static final long serialVersionUID = -7220296068044561221L;
	private Iterable<PeakPosition> peakPositions;
	private String name;
	private String identfier;
	private String description;

	public PeakList(Iterable<PeakPosition> peakPositions, String identfier, String name, String description) {

		this.peakPositions = peakPositions;
		this.name = name;
		this.identfier = identfier;
		this.description = description;
	}

	@Override
	public Iterable<PeakPosition> getResult() {

		return peakPositions;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getIdentifier() {

		return identfier;
	}

	@Override
	public String getDescription() {

		return description;
	}
}
