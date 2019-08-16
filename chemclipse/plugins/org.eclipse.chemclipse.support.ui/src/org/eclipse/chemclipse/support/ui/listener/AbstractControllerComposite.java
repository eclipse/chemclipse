/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractControllerComposite extends Composite implements IFlowController, IStatusUpdater, ISaveController {

	private List<INextListener> nextListeners;
	private List<INextListener> nextSectionListeners;
	private List<IPreviousListener> previousListeners;
	private List<IProcessListener> processListeners;
	private ISaveListener saveListener;

	public AbstractControllerComposite(Composite parent, int style) {
		super(parent, style);
		nextListeners = new ArrayList<INextListener>();
		nextSectionListeners = new ArrayList<INextListener>();
		previousListeners = new ArrayList<IPreviousListener>();
		processListeners = new ArrayList<IProcessListener>();
		saveListener = null;
	}

	@Override
	public void addNextListener(INextListener nextListener) {

		nextListeners.add(nextListener);
	}

	@Override
	public void removeNextListener(INextListener nextListener) {

		nextListeners.remove(nextListener);
	}

	@Override
	public void addNextSectionListener(INextListener nextListener) {

		nextSectionListeners.add(nextListener);
	}

	@Override
	public void removeNextSectionListener(INextListener nextListener) {

		nextSectionListeners.remove(nextListener);
	}

	@Override
	public void addPreviousListener(IPreviousListener previousListener) {

		previousListeners.add(previousListener);
	}

	@Override
	public void removePreviousListener(IPreviousListener previousListener) {

		previousListeners.remove(previousListener);
	}

	@Override
	public void addProcessListener(IProcessListener processListener) {

		processListeners.add(processListener);
	}

	@Override
	public void removeProcessListener(IProcessListener processListener) {

		previousListeners.remove(processListener);
	}

	@Override
	public void setSaveListener(ISaveListener saveListener) {

		this.saveListener = saveListener;
	}

	protected void fireUpdateNext() {

		for(INextListener nextListener : nextListeners) {
			nextListener.nextAction();
		}
	}

	protected void fireUpdateNextLims() {

		for(INextListener nextListener : nextSectionListeners) {
			nextListener.nextAction();
		}
	}

	protected void fireUpdatePrevious() {

		for(IPreviousListener previousListener : previousListeners) {
			previousListener.previousAction();
		}
	}

	protected void fireSave() {

		if(saveListener != null) {
			saveListener.save();
		}
	}

	protected void processAction() {

		for(IProcessListener processListener : processListeners) {
			processListener.processAction();
		}
	}
}
