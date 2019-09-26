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
package org.eclipse.chemclipse.model.methods;

import java.util.Iterator;

public interface IProcessMethod extends Iterable<IProcessEntry> {

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
	String getName();

	/**
	 * Sets the name of this {@link IProcessMethod}
	 * 
	 * @param name
	 * @throws IllegalStateException
	 *             when {@link #isReadOnly()} return true
	 */
	void setName(String name) throws IllegalStateException;

	/**
	 * The category is used to group similar methods
	 * 
	 * @return the category
	 */
	String getCategory();

	/**
	 * Sets the category of this {@link IProcessMethod}
	 * 
	 * @param name
	 * @throws IllegalStateException
	 *             when {@link #isReadOnly()} return true
	 */
	void setCategory(String category) throws IllegalStateException;

	/**
	 * The operator is the person who has created / currently manages this method
	 * 
	 * @return the operator of the method
	 */
	String getOperator();

	/**
	 * Set the operator of this {@link IProcessMethod}
	 * 
	 * @param operator
	 * @throws IllegalStateException
	 *             when {@link #isReadOnly()} return true
	 */
	void setOperator(String operator) throws IllegalStateException;

	/**
	 * 
	 * @return the human readable description of this method
	 */
	String getDescription();

	/**
	 * set the description of this {@link IProcessEntry}
	 * 
	 * @param description
	 * 
	 * @throws IllegalStateException
	 *             when {@link #isReadOnly()} return true
	 * 
	 */
	void setDescription(String description) throws IllegalStateException;

	/**
	 * adds an entry to this processmethod
	 * 
	 * @param processEntry
	 * @throws IllegalStateException
	 *             when {@link #isReadOnly()} return true
	 */
	void addProcessEntry(IProcessEntry processEntry) throws IllegalStateException;

	/**
	 * removes the given entry from this {@link IProcessMethod}
	 * 
	 * @param processEntry
	 * @throws IllegalStateException
	 *             when {@link #isReadOnly()} return true
	 */
	void removeProcessEntry(IProcessEntry processEntry) throws IllegalStateException;

	/**
	 * 
	 * @return true if this is an unmodifiable {@link IProcessMethod}
	 */
	default boolean isReadOnly() {

		return false;
	}

	int getNumberOfEntries();

	static IProcessMethod unmodifiable(IProcessMethod delegate) {

		return new IProcessMethod() {

			@Override
			public Iterator<IProcessEntry> iterator() {

				return delegate.iterator();
			}

			@Override
			public void setOperator(String operator) throws IllegalStateException {

				throw new IllegalStateException("this process method is readonly");
			}

			@Override
			public void setName(String name) throws IllegalStateException {

				throw new IllegalStateException("this process method is readonly");
			}

			@Override
			public void setDescription(String description) throws IllegalStateException {

				throw new IllegalStateException("this process method is readonly");
			}

			@Override
			public void setCategory(String category) throws IllegalStateException {

				throw new IllegalStateException("this process method is readonly");
			}

			@Override
			public void removeProcessEntry(IProcessEntry processEntry) throws IllegalStateException {

				throw new IllegalStateException("this process method is readonly");
			}

			@Override
			public String getOperator() {

				return delegate.getOperator();
			}

			@Override
			public String getName() {

				return delegate.getName();
			}

			@Override
			public String getDescription() {

				return delegate.getDescription();
			}

			@Override
			public String getCategory() {

				return delegate.getCategory();
			}

			@Override
			public void addProcessEntry(IProcessEntry processEntry) throws IllegalStateException {

				throw new IllegalStateException("this process method is readonly");
			}

			@Override
			public boolean isReadOnly() {

				return true;
			}

			@Override
			public int getNumberOfEntries() {

				return delegate.getNumberOfEntries();
			}

			@Override
			public boolean equals(Object obj) {

				return delegate.equals(obj);
			}

			@Override
			public int hashCode() {

				return delegate.hashCode();
			}

			@Override
			public String getUUID() {

				return delegate.getUUID();
			}
		};
	}
}