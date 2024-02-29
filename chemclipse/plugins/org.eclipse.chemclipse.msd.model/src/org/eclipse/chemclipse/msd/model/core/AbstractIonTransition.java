/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * This abstract class describes a ion transition of e.g. a triple quadrupole instrument QqQ.
 */
public abstract class AbstractIonTransition implements IIonTransition {

	private static final long serialVersionUID = 96311762449505536L;
	//
	private String compoundName = "";
	private double q1StartIon;
	private double q1StopIon;
	private double q3StartIon;
	private double q3StopIon;
	private double collisionEnergy;
	private double q1Resolution;
	private double q3Resolution;
	private int transitionGroup;
	private int dwell;
	//
	private int q1Ion;
	private double q3Ion;

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
	protected AbstractIonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		this(filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup, "");
	}

	protected AbstractIonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup) {

		this(filter1Ion, filter1Ion, filter3Ion, filter3Ion, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup, "");
	}

	protected AbstractIonTransition(double filter1Ion, double filter3Ion, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup, String compoundName) {

		this(filter1Ion, filter1Ion, filter3Ion, filter3Ion, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup, compoundName);
	}

	protected AbstractIonTransition(IIonTransition ionTransition, String compoundName) {

		this(ionTransition.getQ1StartIon(), ionTransition.getQ1StopIon(), ionTransition.getQ3StartIon(), ionTransition.getQ3StopIon(), ionTransition.getCollisionEnergy(), ionTransition.getQ1Resolution(), ionTransition.getQ3Resolution(), ionTransition.getTransitionGroup(), compoundName);
	}

	protected AbstractIonTransition(double filter1FirstIon, double filter1LastIon, double filter3FirstIon, double filter3LastIon, double collisionEnergy, double filter1Resolution, double filter3Resolution, int transitionGroup, String compoundName) {

		this.compoundName = compoundName;
		this.q1StartIon = filter1FirstIon;
		this.q1StopIon = filter1LastIon;
		this.q3StartIon = filter3FirstIon;
		this.q3StopIon = filter3LastIon;
		this.collisionEnergy = collisionEnergy;
		this.q1Resolution = filter1Resolution;
		this.q3Resolution = filter3Resolution;
		this.transitionGroup = transitionGroup;
		//
		q1Ion = AbstractIon.getIon((filter1FirstIon + filter1LastIon) / 2.0d);
		q3Ion = AbstractIon.getIon((filter3FirstIon + filter3LastIon) / 2.0d, 1);
		//
		dwell = 100; // Default value
	}

	@Override
	public String getCompoundName() {

		return compoundName;
	}

	@Override
	public void setCompoundName(String compoundName) {

		if(compoundName != null) {
			this.compoundName = compoundName;
		}
	}

	@Override
	public double getQ1Resolution() {

		return q1Resolution;
	}

	@Override
	public double getQ3Resolution() {

		return q3Resolution;
	}

	@Override
	public double getQ1StartIon() {

		return q1StartIon;
	}

	@Override
	public double getQ1StopIon() {

		return q1StopIon;
	}

	@Override
	public int getQ1Ion() {

		return q1Ion;
	}

	@Override
	public double getDeltaQ1Ion() {

		return q1StopIon - q1StartIon;
	}

	@Override
	public double getQ3StartIon() {

		return q3StartIon;
	}

	@Override
	public double getQ3StopIon() {

		return q3StopIon;
	}

	@Override
	public double getQ3Ion() {

		return q3Ion;
	}

	@Override
	public double getDeltaQ3Ion() {

		return q3StopIon - q3StartIon;
	}

	@Override
	public double getCollisionEnergy() {

		return collisionEnergy;
	}

	@Override
	public int getTransitionGroup() {

		return transitionGroup;
	}

	@Override
	public int getDwell() {

		return dwell;
	}

	@Override
	public void setDwell(int dwell) {

		this.dwell = dwell;
	}

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
		return compoundName.equals(other.getCompoundName()) && q1StartIon == other.getQ1StartIon() && q1StopIon == other.getQ1StopIon() && q3StartIon == other.getQ3StartIon() && q3StopIon == other.getQ3StopIon() && collisionEnergy == other.getCollisionEnergy() && transitionGroup == other.getTransitionGroup() && q1Resolution == other.getQ1Resolution() && q3Resolution == other.getQ3Resolution();
	}

	@Override
	public int hashCode() {

		return compoundName.hashCode() + 7 * Double.valueOf(q1StartIon).hashCode() + 11 * Double.valueOf(q1StopIon).hashCode() + 13 * Double.valueOf(q3StartIon).hashCode() + 17 * Double.valueOf(q3StopIon).hashCode() + 13 * Double.valueOf(collisionEnergy).hashCode() + 11 * Integer.valueOf(transitionGroup).hashCode() + 7 * Double.valueOf(q1Resolution).hashCode() + 11 * Double.valueOf(q3Resolution).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("compoundName=" + compoundName);
		builder.append(",");
		builder.append("q1StartIon=" + q1StartIon);
		builder.append(",");
		builder.append("q1StopIon=" + q1StopIon);
		builder.append(",");
		builder.append("q1Resolution=" + q1Resolution);
		builder.append(",");
		builder.append("q3StartIon=" + q3StartIon);
		builder.append(",");
		builder.append("q3StopIon=" + q3StopIon);
		builder.append(",");
		builder.append("q3Resolution=" + q3Resolution);
		builder.append(",");
		builder.append("collisionEnergy=" + collisionEnergy);
		builder.append(",");
		builder.append("transitionGroup=" + transitionGroup);
		builder.append("]");
		return builder.toString();
	}
}