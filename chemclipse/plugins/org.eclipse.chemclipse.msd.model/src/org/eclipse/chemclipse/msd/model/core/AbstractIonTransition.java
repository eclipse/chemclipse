/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * This abstract class describes a ion transition of e.g. a triple quadrupole instrument QqQ.
 */
public abstract class AbstractIonTransition implements IIonTransition {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 96311762449505536L;
	private double filter1FirstIon;
	private double filter1LastIon;
	private double filter3FirstIon;
	private double filter3LastIon;
	private double collisionEnergy;
	private double filter1Resolution;
	private double filter3Resolution;
	private int transitionGroup;
	//
	private int filter1Ion;
	private int filter3Ion;

	/**
	 * The transition has no methods to set the filter ions and the collision energy.
	 * These values are used as the identity of the transition. Each transition
	 * could be referenced by an ion, hence they should be read only after creation.
	 * 
	 * @param filter1FirstIon
	 * @param filter1LastIon
	 * @param filter3FirstIon
	 * @param filter3LastIon
	 * @param collisionEnergy
	 * @param filter1Resolution
	 * @param filter3Resolution
	 * @param transitionGroup
	 */
	public AbstractIonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		this.filter1FirstIon = filter1FirstIon;
		this.filter1LastIon = filter1LastIon;
		this.filter3FirstIon = filter3FirstIon;
		this.filter3LastIon = filter3LastIon;
		this.collisionEnergy = collisionEnergy;
		this.filter1Resolution = filter1Resolution;
		this.filter3Resolution = filter3Resolution;
		this.transitionGroup = transitionGroup;
		//
		filter1Ion = (int)Math.round((filter1FirstIon + filter1LastIon) / 2.0d);
		filter3Ion = (int)Math.round((filter3FirstIon + filter3LastIon) / 2.0d);
	}

	/**
	 * See other constructor.
	 * 
	 * @param filter1Ion
	 * @param filter3Ion
	 * @param collisionEnergy
	 * @param filter1Resolution
	 * @param filter3Resolution
	 * @param transitionGroup
	 */
	public AbstractIonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		this(filter1Ion, filter1Ion, filter3Ion, filter3Ion, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
	}

	@Override
	public double getFilter1Resolution() {

		return filter1Resolution;
	}

	@Override
	public double getFilter3Resolution() {

		return filter3Resolution;
	}

	@Override
	public double getFilter1FirstIon() {

		return filter1FirstIon;
	}

	@Override
	public double getFilter1LastIon() {

		return filter1LastIon;
	}

	@Override
	public int getFilter1Ion() {

		return filter1Ion;
	}

	@Override
	public double getDeltaFilter1Ion() {

		return filter1LastIon - filter1FirstIon;
	}

	@Override
	public double getFilter3FirstIon() {

		return filter3FirstIon;
	}

	@Override
	public double getFilter3LastIon() {

		return filter3LastIon;
	}

	@Override
	public int getFilter3Ion() {

		return filter3Ion;
	}

	@Override
	public double getDeltaFilter3Ion() {

		return filter3LastIon - filter3FirstIon;
	}

	@Override
	public double getCollisionEnergy() {

		return collisionEnergy;
	}

	@Override
	public int getTransitionGroup() {

		return transitionGroup;
	}

	// -----------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		AbstractIonTransition other = (AbstractIonTransition)otherObject;
		return filter1FirstIon == other.getFilter1FirstIon() && filter1LastIon == other.getFilter1LastIon() && filter3FirstIon == other.getFilter3FirstIon() && filter3LastIon == other.getFilter3LastIon() && collisionEnergy == other.getCollisionEnergy() && transitionGroup == other.getTransitionGroup() && filter1Resolution == other.getFilter1Resolution() && filter3Resolution == other.getFilter3Resolution();
	}

	@Override
	public int hashCode() {

		return 7 * new Double(filter1FirstIon).hashCode() + 11 * new Double(filter1LastIon).hashCode() + 13 * new Double(filter3FirstIon).hashCode() + 17 * new Double(filter3LastIon).hashCode() + 13 * new Double(collisionEnergy).hashCode() + 11 * new Integer(transitionGroup).hashCode() + 7 * new Double(filter1Resolution).hashCode() + 11 * new Double(filter3Resolution).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("filter1FirstIon=" + filter1FirstIon);
		builder.append(",");
		builder.append("filter1LastIon=" + filter1LastIon);
		builder.append(",");
		builder.append("filter1Resolution=" + filter1Resolution);
		builder.append(",");
		builder.append("filter3FirstIon=" + filter3FirstIon);
		builder.append(",");
		builder.append("filter3LastIon=" + filter3LastIon);
		builder.append(",");
		builder.append("filter3Resolution=" + filter3Resolution);
		builder.append(",");
		builder.append("collisionEnergy=" + collisionEnergy);
		builder.append(",");
		builder.append("transitionGroup=" + transitionGroup);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
