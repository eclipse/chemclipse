/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.support.l10n.SupportMessages;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class EnhancedTreeViewer extends Composite {

	private static final String POPUP_MENU_ID = "#PopUpMenu"; // $NON-NLS-1$
	private static final String POPUP_MENU_POSTFIX = "PopUpMenu"; // $NON-NLS-1$
	//
	private TreeViewer treeViewer;
	//
	private Clipboard clipboard;

	public EnhancedTreeViewer(Composite parent, int style) {

		super(parent, style);
		setLayout(new FillLayout());
		createControl();
	}

	public TreeViewer getTreeViewer() {

		return treeViewer;
	}

	public void setContentProvider(IContentProvider provider) {

		treeViewer.setContentProvider(provider);
	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {

		treeViewer.setLabelProvider(labelProvider);
	}

	public void setInput(Object input) {

		treeViewer.setInput(input);
	}

	private void createControl() {

		clipboard = new Clipboard(Display.getDefault());
		//
		treeViewer = new TreeViewer(this, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.getTree().setLayout(new FillLayout());
		treeViewer.getTree().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
				}
			}
		});
		treeViewer.getTree().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					copyToClipboard();
				}
			}
		});
		initContextMenu(treeViewer);
	}

	private void initContextMenu(TreeViewer treeViewer) {

		MenuManager menuManager = new MenuManager(POPUP_MENU_ID, getClass().getName() + POPUP_MENU_POSTFIX);
		menuManager.setRemoveAllWhenShown(true);
		/*
		 * Copy to clipboard
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						copyToClipboard();
					}
				};
				action.setText(SupportMessages.labelCopySelectionClipboard);
				manager.add(action);
			}
		});
		//
		Menu menu = menuManager.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(menu);
	}

	private void copyToClipboard() {

		String END_OF_LINE = OperatingSystemUtils.getLineDelimiter();
		StringBuilder builder = new StringBuilder();
		Tree tree = treeViewer.getTree();
		/*
		 * Copy the selected items.
		 */
		Set<TreeItem> rootItems = new HashSet<TreeItem>();
		for(TreeItem treeItem : tree.getSelection()) {
			rootItems.add(getRootItem(treeItem));
		}
		//
		for(TreeItem treeItem : rootItems) {
			copyTreeToClipboard(treeItem, builder, "");
			builder.append(END_OF_LINE);
		}
		/*
		 * If the builder is empty, give a note that items needs to be selected.
		 */
		if(builder.length() == 0) {
			builder.append(SupportMessages.labelCopyLinesInfo);
			builder.append(END_OF_LINE);
		}
		/*
		 * Transfer the selected text (items) to the clipboard.
		 */
		TextTransfer textTransfer = TextTransfer.getInstance();
		Object[] data = new Object[]{builder.toString()};
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}

	private TreeItem getRootItem(TreeItem treeItem) {

		TreeItem treeItemx = treeItem;
		while(treeItemx.getParentItem() != null) {
			treeItemx = treeItemx.getParentItem();
		}
		return treeItemx;
	}

	private void copyTreeToClipboard(TreeItem treeItem, StringBuilder builder, String intend) {

		String DELIMITER = OperatingSystemUtils.TAB;
		String END_OF_LINE = OperatingSystemUtils.getLineDelimiter();
		//
		if(!treeItem.getText().equals("")) {
			builder.append(intend + treeItem.getText());
			builder.append(END_OF_LINE);
		}
		//
		for(TreeItem treeSubItem : treeItem.getItems()) {
			copyTreeToClipboard(treeSubItem, builder, intend + DELIMITER);
		}
	}
}
