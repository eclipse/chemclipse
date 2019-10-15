/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - enhance method definition, add readonly support
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;

public interface IProcessMethod extends ProcessEntryContainer {

	/**
	 * 
	 * @return returns the methods UUID to identify the method across file-systems
	 */
	String getUUID();

	/**
	 * The name is used to display a label to the user
	 * 
	 * @return the human readable label/name
	 */
	@Override
	String getName();

	/**
	 * The category is used to group similar methods
	 * 
	 * @return the category
	 */
	String getCategory();

	/**
	 * The operator is the person who has created / currently manages this method
	 * 
	 * @return the operator of the method
	 */
	String getOperator();

	/**
	 * 
	 * @return the human readable description of this method
	 */
	@Override
	String getDescription();

	/**
	 * a method marked as final is one that is approved or otherwise locked for further modifications and will stay constant over time
	 * 
	 * @return <code>true</code> if this is a final method or <code>false</code> otherwise
	 */
	boolean isFinal();

	/**
	 * a method might be defined in the context of valid types e.g. for Chromatography (MSD, WSD, CSD) or NMR (FID, NMR)
	 * 
	 * @return the {@link DataCategory}s that are valid for this methods context
	 */
	Set<DataCategory> getDataCategories();

	/**
	 * Each {@link IProcessMethod} can carry a set of metadata pairs to store user metadata
	 * 
	 * @return a map of key/value pairs of the metadata
	 */
	Map<String, String> getMetaData();

	/**
	 * Compares that this process methods content equals the other process method, the default implementation compares
	 * {@link #getName()},
	 * {@link #getCategory()},
	 * {@link #getDescription()},
	 * {@link #getOperator()},
	 * {@link #isFinal()}
	 * and all contained {@link IProcessEntry}s
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare different instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1},
	 * 
	 * {@link #getDataCategories()} are not compared because that does not contribute to the content but is only a hint
	 * 
	 * @param other
	 * @return
	 */
	default boolean contentEquals(IProcessMethod other, boolean includeMetadata) {

		if(other == null) {
			return false;
		}
		if(other == this) {
			return true;
		}
		if(isFinal() != other.isFinal()) {
			return false;
		}
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(!getCategory().equals(other.getCategory())) {
			return false;
		}
		if(!getDescription().equals(other.getDescription())) {
			return false;
		}
		if(!getOperator().equals(other.getOperator())) {
			return false;
		}
		if(getNumberOfEntries() != other.getNumberOfEntries()) {
			return false;
		}
		if(includeMetadata && !getMetaData().equals(other.getMetaData())) {
			return false;
		}
		return entriesEquals(other);
	}
}