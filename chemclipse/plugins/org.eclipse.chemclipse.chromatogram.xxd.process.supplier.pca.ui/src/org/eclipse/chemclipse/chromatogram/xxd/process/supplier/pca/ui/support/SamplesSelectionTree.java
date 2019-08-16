/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SamplesSelectionTree {

	private List<IPcaResult> pcaResults = new ArrayList<>();

	public SamplesSelectionTree() {
	}

	public void create(Composite parent) {

		Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		TreeItem groupTreeItem;
		String groupName;
		boolean isSelectSample = false;
		TreeItem treeItem;
		Iterator<IPcaResult> it = pcaResults.iterator();
		if(it.hasNext()) {
			/*
			 * set first branch, which contains group name
			 */
			IPcaResult pcaResult = it.next();
			groupTreeItem = new TreeItem(tree, SWT.None);
			groupName = pcaResult.getGroupName();
			if(groupName != null) {
				groupTreeItem.setText(groupName);
			} else {
				groupTreeItem.setText("----");
				groupName = "";
			}
			treeItem = new TreeItem(groupTreeItem, SWT.None);
			setSampleTreeItem(pcaResult, treeItem);
			isSelectSample = isSelectSample || pcaResult.isDisplayed();
			groupTreeItem.setExpanded(true);
			while(it.hasNext()) {
				pcaResult = it.next();
				if(groupName.equals(pcaResult.getGroupName())) {
					treeItem = new TreeItem(groupTreeItem, SWT.None);
					setSampleTreeItem(pcaResult, treeItem);
					isSelectSample = isSelectSample || pcaResult.isDisplayed();
				} else {
					groupTreeItem.setChecked(isSelectSample);
					/*
					 * set branch, which contains group name
					 */
					groupTreeItem = new TreeItem(tree, SWT.None);
					isSelectSample = false;
					groupName = pcaResult.getGroupName();
					if(groupName != null) {
						groupTreeItem.setText(groupName);
					} else {
						groupTreeItem.setText("----");
					}
					treeItem = new TreeItem(groupTreeItem, SWT.None);
					setSampleTreeItem(pcaResult, treeItem);
					isSelectSample = isSelectSample || pcaResult.isDisplayed();
				}
			}
			groupTreeItem.setChecked(isSelectSample);
		}
		/*
		 * add check listener
		 */
		tree.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if(event.detail != SWT.CHECK) {
					return;
				}
				TreeItem item = (TreeItem)event.item;
				boolean isChecked = item.getChecked();
				/*
				 * set selected item
				 */
				IPcaResult pcaResult = (IPcaResult)item.getData();
				if(pcaResult != null) {
					pcaResult.setDisplayed(isChecked);
				}
				/*
				 * set children are checked according to selected item
				 */
				TreeItem[] children = item.getItems();
				for(TreeItem treeItem : children) {
					treeItem.setChecked(isChecked);
					pcaResult = (IPcaResult)treeItem.getData();
					if(pcaResult != null) {
						pcaResult.setDisplayed(isChecked);
					}
				}
				/*
				 * set parent if any child of parent is set checked, parent is checked also.
				 */
				TreeItem parentItem = item.getParentItem();
				if(parentItem != null) {
					TreeItem[] sibs = parentItem.getItems();
					boolean checkParentItem = false;
					for(TreeItem sib : sibs) {
						pcaResult = (IPcaResult)sib.getData();
						if(pcaResult != null) {
							checkParentItem = checkParentItem || pcaResult.isDisplayed();
						}
						parentItem.setChecked(checkParentItem);
					}
				}
			}
		});
	}

	private void setSampleTreeItem(IPcaResult pcaResult, TreeItem item) {

		item.setChecked(pcaResult.isDisplayed());
		item.setText(pcaResult.getName());
		item.setData(pcaResult);
	}

	public void update(IPcaResultsVisualization resultsVisualization) {

		/*
		 * clear all
		 */
		pcaResults.clear();
		/*
		 * insert and sort samples
		 */
		pcaResults.addAll(resultsVisualization.getPcaResultList());
		PcaUtils.sortPcaResultsByName(pcaResults);
		PcaUtils.sortPcaResultsByGroup(pcaResults);
	}
}
