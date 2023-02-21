/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.alignment.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.NoRetentionIndexAvailableException;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.RetentionIndexExistsException;
import org.eclipse.chemclipse.chromatogram.alignment.model.exceptions.RetentionIndexValueException;

/**
 * @author eselmeister
 */
public class RetentionIndices implements IRetentionIndices {

	private List<IRetentionIndex> retentionIndices;
	private int actualRetentionIndex = 1;
	private RetentionIndexComparator retentionIndexComparator;

	public RetentionIndices() {

		this.retentionIndices = new ArrayList<>(10);
		retentionIndexComparator = new RetentionIndexComparator();
	}

	@Override
	public void addRetentionIndex(final IRetentionIndex retentionIndex) throws RetentionIndexExistsException, RetentionIndexValueException {

		/*
		 * If those two test are failing, an exception will be thrown.
		 */
		checkRetentionIndexExists(retentionIndex);
		checkRetentionIndexSortOrder(retentionIndex);
		/*
		 * If no exception has been thrown until now, the new retentionIndex can
		 * be added to the list.
		 */
		this.retentionIndices.add(retentionIndex);
		/*
		 * It is not assumed, that the ArrayList will be bigger than 100
		 * entries. As for that, a ascending sorting concerning the index is
		 * applicable.
		 */
		Collections.sort(this.retentionIndices, retentionIndexComparator);
		this.actualRetentionIndex = getRetentionIndexEntry(retentionIndex);
	}

	/**
	 * Checks, if the given retentionIndex still exists in the list. The method
	 * equals is redefined in the class RetentionIndex.
	 * 
	 * @param retentionIndex
	 * @throws RetentionIndexExistsException
	 */
	private void checkRetentionIndexExists(final IRetentionIndex retentionIndex) throws RetentionIndexExistsException {

		for(IRetentionIndex ri : retentionIndices) {
			if(retentionIndex.equals(ri)) {
				throw new RetentionIndexExistsException();
			}
		}
	}

	/**
	 * Checks if the new
	 * 
	 * @param retentionIndex
	 * @throws RetentionIndexExistsException
	 */
	private void checkRetentionIndexSortOrder(final IRetentionIndex retentionIndex) throws RetentionIndexValueException {

		int previousRetentionTime = calculatePreviousRetentionTime(retentionIndex);
		int nextRetentionTime = calculateNextRetentionTime(retentionIndex);
		/*
		 * Check now if the indices and retention times are correct.
		 */
		if(retentionIndex.getRetentionTime() <= previousRetentionTime || retentionIndex.getRetentionTime() >= nextRetentionTime) {
			throw new RetentionIndexValueException();
		}
	}

	/**
	 * This method is used in checkRetentionIndexSortOrder.
	 * 
	 * @param retentionIndex
	 * @return int
	 */
	private int calculatePreviousRetentionTime(final IRetentionIndex retentionIndex) {

		IRetentionIndex previous;
		int previousRetentionTime = 0;
		/*
		 * If no previous retention index is available, the lower retention time
		 * limit must be 0. But if there is just one entry in the ArrayList,
		 * getPreviousRetentionIndex(retentionIndex.getIndex()) would return
		 * NoRetentionIndexAvailableException. So, check before trying, if the
		 * ArrayList contains more than one entries.
		 */
		// If there are at least 2 entries in the ArrayList.
		if(this.retentionIndices.size() > 1) {
			try {
				previous = getPreviousRetentionIndex(retentionIndex.getIndex());
				previousRetentionTime = previous.getRetentionTime();
			} catch(NoRetentionIndexAvailableException e) {
				// If no previous retention index could be found, the lower
				// border must be 0.
				previousRetentionTime = 0;
			}
			// If there is only 1 entry in the ArrayList.
		} else {
			try {
				// Get the one and only entry from the list.
				previous = getRetentionIndex(1);
				/*
				 * If the retention index, which should be inserted, has a
				 * greater retention time than the previous, the lower border
				 * must be previous.getRetentionTime(). If the retention time is
				 * lower, than lower border must be 0.
				 */
				if(retentionIndex.getRetentionTime() > previous.getRetentionTime()) {
					previousRetentionTime = previous.getRetentionTime();
				} else {
					previousRetentionTime = 0;
				}
				// This should not happen, but if no retention index could be
				// detected, the previousRetentionTime = 0.
			} catch(NoRetentionIndexAvailableException e) {
				previousRetentionTime = 0;
			}
		}
		return previousRetentionTime;
	}

	/**
	 * This method is used in checkRetentionIndexSortOrder.
	 * 
	 * @param retentionIndex
	 * @return int
	 */
	private int calculateNextRetentionTime(final IRetentionIndex retentionIndex) {

		IRetentionIndex next;
		int nextRetentionTime = 0;
		/*
		 * If no next retention index is available, the upper retention time
		 * limit must be max value. But if there is just one entry in the
		 * ArrayList, getNextRetentionIndex(retentionIndex.getIndex()) would
		 * return NoRetentionIndexAvailableException. So, check before trying,
		 * if the ArrayList contains more than one entries.
		 */
		// If there are at least 2 entries in the ArrayList.
		if(this.retentionIndices.size() > 1) {
			try {
				next = getNextRetentionIndex(retentionIndex.getIndex());
				nextRetentionTime = next.getRetentionTime();
			} catch(NoRetentionIndexAvailableException e) {
				// If no next retention index could be found, the upper border
				// must be RetentionIndex.MAX_RETENTIONTIME.
				nextRetentionTime = RetentionIndex.MAX_RETENTION_TIME;
			}
			// If there is only 1 entry in the ArrayList.
		} else {
			try {
				// Get the one and only entry from the list.
				next = getRetentionIndex(1);
				/*
				 * If the retention index, which should be inserted, has a
				 * higher retention time than the previous, the upper border
				 * must be RetentionIndex.MAX_RETENTIONTIME. If the retention
				 * time is lower, than lower border must be
				 * next.getRetentionTime().
				 */
				if(retentionIndex.getRetentionTime() > next.getRetentionTime()) {
					nextRetentionTime = RetentionIndex.MAX_RETENTION_TIME;
				} else {
					nextRetentionTime = next.getRetentionTime();
				}
				// This should not happen, but if no retention index could be
				// detected, the previousRetentionTime =
				// RetentionIndex.MAX_RETENTIONTIME.
			} catch(NoRetentionIndexAvailableException e) {
				nextRetentionTime = RetentionIndex.MAX_RETENTION_TIME;
			}
		}
		return nextRetentionTime;
	}

	@Override
	public IRetentionIndex getActualRetentionIndex() throws NoRetentionIndexAvailableException {

		return getRetentionIndex(actualRetentionIndex);
	}

	@Override
	public IRetentionIndex getFirstRetentionIndex() throws NoRetentionIndexAvailableException {

		return getRetentionIndex(1);
	}

	@Override
	public IRetentionIndex getLastRetentionIndex() throws NoRetentionIndexAvailableException {

		return getRetentionIndex(retentionIndices.size());
	}

	@Override
	public IRetentionIndex getPreviousRetentionIndex() throws NoRetentionIndexAvailableException {

		int previousIndex = actualRetentionIndex;
		return getRetentionIndex(--previousIndex);
	}

	@Override
	public IRetentionIndex getNextRetentionIndex() throws NoRetentionIndexAvailableException {

		int nextIndex = actualRetentionIndex;
		return getRetentionIndex(++nextIndex);
	}

	/**
	 * Returns an IRetentionIndex by the entry specified. Entry is the number of
	 * the entry. It is not handled like an Array (where 1 is the second entry).
	 * Use 1 to get the first entry, 2 to get the second and so on.
	 * 
	 * @param entry
	 *            - 1 first entry, ...
	 * @return IRetentionIndex
	 * @throws NoRetentionIndexAvailableException
	 */
	private IRetentionIndex getRetentionIndex(final int entry) throws NoRetentionIndexAvailableException {

		int position = entry;
		/*
		 * At least the first entry from the list should be returned. If not it
		 * can be assumed that no RetentionIndex is available.
		 */
		if(entry < 1) {
			throw new NoRetentionIndexAvailableException();
		}
		/*
		 * If the requested entry is greater than the size of the
		 * retentionIndice list or the list is still empty, no RetentionIndex
		 * can be returned.
		 */
		if(!this.retentionIndices.isEmpty() && entry <= this.retentionIndices.size()) {
			// Set the new actualRetentionIndex
			actualRetentionIndex = entry;
			return this.retentionIndices.get(--position);
		} else {
			throw new NoRetentionIndexAvailableException();
		}
	}

	public int getRetentionIndexEntry(final IRetentionIndex retentionIndex) {

		IRetentionIndex actIndex;
		int index = 1; // default
		skip:
		for(int i = 1; i <= this.retentionIndices.size(); i++) {
			try {
				actIndex = getRetentionIndex(i);
			} catch(NoRetentionIndexAvailableException e) {
				// If the RetentionIndex was not available return 1.
				break skip;
			}
			if(retentionIndex.getIndex() == actIndex.getIndex()) {
				index = i;
				break skip;
			}
		}
		return index;
	}

	@Override
	public IRetentionIndex getPreviousRetentionIndex(final float index) throws NoRetentionIndexAvailableException {

		IRetentionIndex actIndex;
		// The list needs not to be sorted, cause it's still sorted ascending by
		// index values.
		for(int i = 1; i <= this.retentionIndices.size(); i++) {
			actIndex = getRetentionIndex(i);
			if(actIndex.getIndex() >= index) {
				if(i == 1) {
					throw new NoRetentionIndexAvailableException();
				} else {
					return getRetentionIndex(--i);
				}
			}
		}
		// This state should never be entered.
		throw new NoRetentionIndexAvailableException();
	}

	@Override
	public IRetentionIndex getPreviousRetentionIndex(final int retentionTime) throws NoRetentionIndexAvailableException {

		IRetentionIndex actIndex;
		// The list needs not to be sorted, cause it's still sorted ascending by
		// index values.
		for(int i = 1; i <= this.retentionIndices.size(); i++) {
			actIndex = getRetentionIndex(i);
			if(actIndex.getRetentionTime() >= retentionTime) {
				if(i == 1) {
					throw new NoRetentionIndexAvailableException();
				} else {
					return getRetentionIndex(--i);
				}
			}
		}
		// This state should never be entered.
		throw new NoRetentionIndexAvailableException();
	}

	@Override
	public IRetentionIndex getNextRetentionIndex(final float index) throws NoRetentionIndexAvailableException {

		IRetentionIndex actIndex;
		// The list needs not to be sorted, cause it's still sorted ascending by
		// index values.
		for(int i = this.retentionIndices.size(); i >= 1; i--) {
			actIndex = getRetentionIndex(i);
			if(actIndex.getIndex() <= index) {
				if(i == this.retentionIndices.size()) {
					throw new NoRetentionIndexAvailableException();
				} else {
					return getRetentionIndex(++i);
				}
			}
		}
		// This state should never be entered.
		throw new NoRetentionIndexAvailableException();
	}

	@Override
	public IRetentionIndex getNextRetentionIndex(final int retentionTime) throws NoRetentionIndexAvailableException {

		IRetentionIndex actIndex;
		// The list needs not to be sorted, cause it's still sorted ascending by
		// index values.
		for(int i = this.retentionIndices.size(); i >= 1; i--) {
			actIndex = getRetentionIndex(i);
			if(actIndex.getRetentionTime() <= retentionTime) {
				if(i == this.retentionIndices.size()) {
					throw new NoRetentionIndexAvailableException();
				} else {
					return getRetentionIndex(++i);
				}
			}
		}
		// This state should never be entered.
		throw new NoRetentionIndexAvailableException();
	}

	@Override
	public void removeRetentionIndex(final float index) {

		int i = 0;
		for(IRetentionIndex ri : this.retentionIndices) {
			i++;
			if(ri.getIndex() == index) {
				retentionIndices.remove(ri);
				setActualRetentionIndex(i);
				break;
			}
		}
	}

	@Override
	public void removeRetentionIndex(final int retentionTime) {

		int i = 0;
		for(IRetentionIndex ri : this.retentionIndices) {
			i++;
			if(ri.getRetentionTime() == retentionTime) {
				retentionIndices.remove(ri);
				setActualRetentionIndex(i);
				break;
			}
		}
	}

	@Override
	public void removeRetentionIndex(final IRetentionIndex retentionIndex) {

		int i = 0;
		for(IRetentionIndex ri : this.retentionIndices) {
			i++;
			if(ri.equals(retentionIndex)) {
				retentionIndices.remove(ri);
				setActualRetentionIndex(i);
				break;
			}
		}
	}

	@Override
	public void removeRetentionIndex(final String name) {

		int i = 1;
		for(IRetentionIndex ri : this.retentionIndices) {
			if(ri.getName().equals(name)) {
				retentionIndices.remove(ri);
				setActualRetentionIndex(i);
				break;
			}
			i++;
		}
	}

	private void setActualRetentionIndex(final int index) {

		int position = index;
		if(index == 1) {
			this.actualRetentionIndex = position;
		} else {
			this.actualRetentionIndex = --position;
		}
	}
}
