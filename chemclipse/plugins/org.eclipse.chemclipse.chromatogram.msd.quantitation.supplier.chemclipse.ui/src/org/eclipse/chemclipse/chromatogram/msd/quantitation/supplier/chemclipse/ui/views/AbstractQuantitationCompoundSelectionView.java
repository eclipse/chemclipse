/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.views;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSelectionView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractQuantitationCompoundSelectionView extends AbstractSelectionView implements IQuantitationCompoundSelectionView {

	private IQuantitationCompound quantitationCompoundMSD;
	private IQuantDatabase database;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	public AbstractQuantitationCompoundSelectionView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@Override
	public IQuantitationCompound getQuantitationCompoundDocument() {

		return quantitationCompoundMSD;
	}

	@Override
	public void setQuantitationCompoundDocument(IQuantitationCompound quantitationCompoundMSD) {

		this.quantitationCompoundMSD = quantitationCompoundMSD;
	}

	@Override
	public IQuantDatabase getDatabase() {

		return database;
	}

	@Override
	public void setDatabase(IQuantDatabase database) {

		this.database = database;
	}

	@Override
	public boolean doUpdate() {

		if(isPartVisible()) {
			return true;
		}
		return false;
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					Object objectDocument = event.getProperty(IChemClipseQuantitationEvents.PROPERTY_QUANTITATION_COMPOUND_DOCUMENT);
					Object objectDatabase = event.getProperty(IChemClipseQuantitationEvents.PROPERTY_DATABASE);
					if(objectDocument instanceof IQuantitationCompound && objectDatabase instanceof IQuantDatabase) {
						/*
						 * Ensures that the objects are valid.
						 */
						quantitationCompoundMSD = (IQuantitationCompound)objectDocument;
						database = (IQuantDatabase)objectDatabase;
						update(quantitationCompoundMSD, database);
					} else {
						/*
						 * Unload the objects.
						 */
						update(null, null);
					}
				}
			};
			eventBroker.subscribe(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_COMPOUND_DOCUMENT_UPDATE, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
