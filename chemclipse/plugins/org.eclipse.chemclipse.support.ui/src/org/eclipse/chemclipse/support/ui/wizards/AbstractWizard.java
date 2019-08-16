/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.l10n.Messages;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public abstract class AbstractWizard extends Wizard implements IFileWizard {

	private static final Logger logger = Logger.getLogger(AbstractWizard.class);
	private ISelection selection;
	private IWizardElements wizardElements;
	private Set<IExtendedWizardPage> wizardPages;

	public AbstractWizard(IWizardElements wizardElements) {
		super();
		setNeedsProgressMonitor(true);
		this.wizardElements = wizardElements;
		this.wizardPages = new HashSet<IExtendedWizardPage>();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		this.selection = selection;
	}

	public ISelection getSelection() {

		return selection;
	}

	@Override
	public void addPage(IWizardPage page) {

		if(page instanceof IExtendedWizardPage) {
			/*
			 * Add the pages to run an automated canFinish check.
			 */
			wizardPages.add((IExtendedWizardPage)page);
		}
		super.addPage(page);
	}

	@Override
	public boolean canFinish() {

		Iterator<IExtendedWizardPage> iterator = wizardPages.iterator();
		while(iterator.hasNext()) {
			/*
			 * Return false if a page can't be finished.
			 */
			if(!iterator.next().canFinish()) {
				return false;
			}
		}
		/*
		 * All validations are passed successfully.
		 */
		return true;
	}

	/**
	 * Returns the wizard elements.
	 * 
	 * @return {@link IWizardElements}
	 */
	public IWizardElements getWizardElements() {

		return wizardElements;
	}

	@Override
	public boolean performFinish() {

		/*
		 * Creates the runnable.
		 */
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					doFinish(monitor);
				} catch(CoreException e) {
					logger.warn(e);
				}
			}
		};
		/*
		 * Run
		 */
		Messages messages = SupportMessages.INSTANCE();
		try {
			getContainer().run(true, false, runnable);
		} catch(InterruptedException e) {
			MessageDialog.openError(getShell(), messages.getMessage(ISupportMessages.PROCESSING_ERROR), messages.getMessage(ISupportMessages.PROCESSING_PROCESS_INTERRUPTED));
			return false;
		} catch(InvocationTargetException e) {
			MessageDialog.openError(getShell(), messages.getMessage(ISupportMessages.PROCESSING_ERROR), messages.getMessage(ISupportMessages.PROCESSING_SOMETHING_WRONG));
			return false;
		}
		return true;
	}
}
