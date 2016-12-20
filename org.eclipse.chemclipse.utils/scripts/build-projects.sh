#!/bin/bash

#*******************************************************************************
# Copyright (c) 2016 Lablicate GmbH.
# 
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
#	Dr. Philip Wenig
#*******************************************************************************/

#
# Workspace
#
workspace=$1
if [ -z $workspace ]; then
	echo "FAILURE: Please select a valid workspace."
	exit 1
fi

if [ ! -d $workspace ]; then
	echo "FAILURE: The selected workspace doesn't exist."
	exit 1
fi

cd $workspace
active=$(pwd)

function build_project {

	folder=$1
	url=$2
	cbi=$3
	#
	# Clone or update.
	#
	cd $active
	if [ ! -d $folder ]; then
		git clone $url
		cd $folder
		git checkout develop
	else
		cd $folder
		git checkout develop
		git pull
	fi
	#
	# Build the repo.
	#
	cd $active
	mvn -f $cbi clean install
}

echo "--------------------------------------------"
echo "Build ChemClipse/OpenChrom Community Edition"
echo "--------------------------------------------"
	echo "Workspace: "$workspace
	#
	# Build order is important.
	#
	build_project 'org.eclipse.chemclipse.chemclipse3rdpl' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipse3rdpl.git' 'org.eclipse.chemclipse.chemclipse3rdpl/chemclipse/cbi/org.eclipse.chemclipse.thirdpartylibraries.cbi/pom.xml'
	build_project 'openchrom3rdpl' 'https://github.com/OpenChrom/openchrom3rdpl.git' 'openchrom3rdpl/openchrom/cbi/net.openchrom.thirdpartylibraries.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.chemclipsecore' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipsecore.git' 'org.eclipse.chemclipse.chemclipsecore/chemclipse/cbi/org.eclipse.chemclipse.cbi/pom.xml'
	build_project 'msqbatlibs' 'https://github.com/OpenChrom/msqbatlibs.git' 'msqbatlibs/openchrom/cbi/net.openchrom.msqbatlibs.cbi/pom.xml'
	#
	# Could be processed in parallel
	#
	build_project 'org.eclipse.chemclipse.chemclipsesvg' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipsesvg.git' 'org.eclipse.chemclipse.chemclipsesvg/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.svg.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.compmsincos' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.compmsincos.git' 'org.eclipse.chemclipse.compmsincos/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.sncalcstein' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.sncalcstein.git' 'org.eclipse.chemclipse.sncalcstein/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.stein.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.chemclipsemsx' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipsemsx.git' 'org.eclipse.chemclipse.chemclipsemsx/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.openchromx.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filemsdidentifier' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filemsdidentifier.git' 'org.eclipse.chemclipse.filemsdidentifier/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.mzxmlmschrom' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.mzxmlmschrom.git' 'org.eclipse.chemclipse.mzxmlmschrom/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.mzxml.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filtermfremover' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filtermfremover.git' 'org.eclipse.chemclipse.filtermfremover/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.matlabparafac' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.matlabparafac.git' 'org.eclipse.chemclipse.matlabparafac/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.identpeakmanual' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.identpeakmanual.git' 'org.eclipse.chemclipse.identpeakmanual/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.manual.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.ocjcampdx' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.ocjcampdx.git' 'org.eclipse.chemclipse.ocjcampdx/chemclipse/cbi/org.eclipse.chemclipse.xxd.converter.supplier.jcampdx.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.officeconnector' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.officeconnector.git' 'org.eclipse.chemclipse.officeconnector/chemclipse/cbi/org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.occhromareport' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.occhromareport.git' 'org.eclipse.chemclipse.occhromareport/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakintegrchems' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakintegrchems.git' 'org.eclipse.chemclipse.peakintegrchems/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.amdismsdata' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.amdismsdata.git' 'org.eclipse.chemclipse.amdismsdata/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.amdis.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.processpeakspca' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.processpeakspca.git' 'org.eclipse.chemclipse.processpeakspca/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.converteraniml' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.converteraniml.git' 'org.eclipse.chemclipse.converteraniml/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.animl.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filtercoda' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filtercoda.git' 'org.eclipse.chemclipse.filtercoda/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.chemclipsebatchj' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipsebatchj.git' 'org.eclipse.chemclipse.chemclipsebatchj/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filterfidzeroset' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filterfidzeroset.git' 'org.eclipse.chemclipse.filterfidzeroset/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.csd.filter.supplier.zeroset.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakdetecchemst' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakdetecchemst.git' 'org.eclipse.chemclipse.peakdetecchemst/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filtersavgolay' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filtersavgolay.git' 'org.eclipse.chemclipse.filtersavgolay/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.xyconverter' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.xyconverter.git' 'org.eclipse.chemclipse.xyconverter/chemclipse/cbi/org.eclipse.chemclipse.csd.converter.supplier.xy.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filterdenoising' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filterdenoising.git' 'org.eclipse.chemclipse.filterdenoising/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakmax' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakmax.git' 'org.eclipse.chemclipse.peakmax/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakdetecmanual' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakdetecmanual.git' 'org.eclipse.chemclipse.peakdetecmanual/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.convertermz5' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.convertermz5.git' 'org.eclipse.chemclipse.convertermz5/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.mz5.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.sncalcdyson' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.sncalcdyson.git' 'org.eclipse.chemclipse.sncalcdyson/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.classifierwnc' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.classifierwnc.git' 'org.eclipse.chemclipse.classifierwnc/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.subtractfilter' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.subtractfilter.git' 'org.eclipse.chemclipse.subtractfilter/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.mzmlconverter' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.mzmlconverter.git' 'org.eclipse.chemclipse.mzmlconverter/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.mzml.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.convertermassbank' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.convertermassbank.git' 'org.eclipse.chemclipse.convertermassbank/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.massbank.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.baselinedetec' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.baselinedetec.git' 'org.eclipse.chemclipse.baselinedetec/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.baselinesnip' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.baselinesnip.git' 'org.eclipse.chemclipse.baselinesnip/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.compmsdistance' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.compmsdistance.git' 'org.eclipse.chemclipse.compmsdistance/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.convertermzdata' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.convertermzdata.git' 'org.eclipse.chemclipse.convertermzdata/chemclipse/cbi/org.eclipse.chemclipse.msd.converter.supplier.mzdata.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakidentbatch' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakidentbatch.git' 'org.eclipse.chemclipse.peakidentbatch/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.xpassmsfilter' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.xpassmsfilter.git' 'org.eclipse.chemclipse.xpassmsfilter/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakdetecthird' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakdetecthird.git' 'org.eclipse.chemclipse.peakdetecthird/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.thirdderivative.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.sumarea' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.sumarea.git' 'org.eclipse.chemclipse.sumarea/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.rtshifter' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.rtshifter.git' 'org.eclipse.chemclipse.rtshifter/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.chemclipsems' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipsems.git' 'org.eclipse.chemclipse.chemclipsems/chemclipse/cbi/org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.filterbackfold' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.filterbackfold.git' 'org.eclipse.chemclipse.filterbackfold/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.cbi/pom.xml'
	build_project 'triplequadidentifier' 'https://github.com/OpenChrom/triplequadidentifier.git' 'triplequadidentifier/openchrom/cbi/net.openchrom.msd.identifier.supplier.triplequad.cbi/pom.xml'
	build_project 'filtermeannormalizer' 'https://github.com/OpenChrom/filtermeannormalizer.git' 'filtermeannormalizer/openchrom/cbi/net.openchrom.chromatogram.xxd.filter.supplier.meannormalizer.cbi/pom.xml'
	build_project 'alfassicompms' 'https://github.com/OpenChrom/alfassicompms.git' 'alfassicompms/openchrom/cbi/net.openchrom.chromatogram.msd.comparison.supplier.alfassi.cbi/pom.xml'
	build_project 'cmsconverter' 'https://github.com/OpenChrom/cmsconverter.git' 'cmsconverter/openchrom/cbi/net.openchrom.msd.converter.supplier.cms.cbi/pom.xml'
	build_project 'knimeconnector' 'https://github.com/OpenChrom/knimeconnector.git' 'knimeconnector/openchrom/cbi/net.openchrom.chromatogram.xxd.process.supplier.knime.cbi/pom.xml'
	build_project 'zipconverter' 'https://github.com/OpenChrom/zipconverter.git' 'zipconverter/openchrom/cbi/net.openchrom.msd.converter.supplier.zip.cbi/pom.xml'
	build_project 'netcdfchromfid' 'https://github.com/OpenChrom/netcdfchromfid.git' 'netcdfchromfid/chemclipse/cbi/net.openchrom.csd.converter.supplier.cdf.cbi/pom.xml'
	build_project 'compmspbm' 'https://github.com/OpenChrom/compmspbm.git' 'compmspbm/openchrom/cbi/net.openchrom.chromatogram.msd.comparison.supplier.pbm.cbi/pom.xml'
	build_project 'excelconverter' 'https://github.com/OpenChrom/excelconverter.git' 'excelconverter/openchrom/cbi/net.openchrom.msd.converter.supplier.excel.cbi/pom.xml'
	build_project 'pdfconverter' 'https://github.com/OpenChrom/pdfconverter.git' 'pdfconverter/openchrom/cbi/net.openchrom.msd.converter.supplier.pdf.cbi/pom.xml'
	build_project 'batmassprocessheatmap' 'https://github.com/OpenChrom/batmassprocessheatmap.git' 'batmassprocessheatmap/openchrom/cbi/org.batmass.xxd.process.supplier.heatmap.cbi/pom.xml'
	build_project 'abifconverter' 'https://github.com/OpenChrom/abifconverter.git' 'abifconverter/openchrom/cbi/net.openchrom.wsd.converter.supplier.abif.cbi/pom.xml'
	build_project 'mgfconverter' 'https://github.com/OpenChrom/mgfconverter.git' 'mgfconverter/openchrom/cbi/net.openchrom.msd.converter.supplier.mgf.cbi/pom.xml'
	build_project 'scanremover' 'https://github.com/OpenChrom/scanremover.git' 'scanremover/openchrom/cbi/net.openchrom.chromatogram.xxd.filter.supplier.scanremover.cbi/pom.xml'
	build_project 'filterunitsumnormalizer' 'https://github.com/OpenChrom/filterunitsumnormalizer.git' 'filterunitsumnormalizer/openchrom/cbi/net.openchrom.chromatogram.xxd.filter.supplier.unitsumnormalizer.cbi/pom.xml'
	build_project 'netcdfmschrom' 'https://github.com/OpenChrom/netcdfmschrom.git' 'netcdfmschrom/chemclipse/cbi/net.openchrom.msd.converter.supplier.cdf.cbi/pom.xml'
	build_project 'gsonconverter' 'https://github.com/OpenChrom/gsonconverter.git' 'gsonconverter/openchrom/cbi/net.openchrom.xxd.converter.supplier.gson.cbi/pom.xml'
	build_project 'openchromcdksupport' 'https://github.com/OpenChrom/openchromcdksupport.git' 'openchromcdksupport/chemclipse/cbi/net.openchrom.chromatogram.msd.identifier.supplier.cdk.cbi/pom.xml'
	build_project 'openchromjzy3d' 'https://github.com/OpenChrom/openchromjzy3d.git' 'openchromjzy3d/openchrom/cbi/net.openchrom.chromatogram.msd.process.supplier.jzy3d.cbi/pom.xml'
	build_project 'filtermultiplier' 'https://github.com/OpenChrom/filtermultiplier.git' 'filtermultiplier/openchrom/cbi/net.openchrom.chromatogram.xxd.filter.supplier.multiplier.cbi/pom.xml'
	build_project 'filterbaselinesubtract' 'https://github.com/OpenChrom/filterbaselinesubtract.git' 'filterbaselinesubtract/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.cbi/pom.xml'
	build_project 'geneident' 'https://github.com/OpenChrom/geneident.git' 'geneident/openchrom/cbi/net.openchrom.wsd.identifier.supplier.geneident.cbi/pom.xml'
	build_project 'baselinedetectic' 'https://github.com/OpenChrom/baselinedetectic.git' 'baselinedetectic/openchrom/cbi/net.openchrom.chromatogram.xxd.baseline.detector.supplier.tic.cbi/pom.xml'
	build_project 'xmutidemscontrol' 'https://github.com/OpenChrom/xmutidemscontrol.git' 'xmutidemscontrol/chemclipse/cbi/cn.edu.xmu.tidems.control.cbi/pom.xml'
	build_project 'filtermediannormalizer' 'https://github.com/OpenChrom/filtermediannormalizer.git' 'filtermediannormalizer/openchrom/cbi/net.openchrom.chromatogram.xxd.filter.supplier.mediannormalizer.cbi/pom.xml'
	build_project 'arwconverter' 'https://github.com/OpenChrom/arwconverter.git' 'arwconverter/openchrom/cbi/net.openchrom.csd.converter.supplier.arw.cbi/pom.xml'
	build_project 'csvconverter' 'https://github.com/OpenChrom/csvconverter.git' 'csvconverter/openchrom/cbi/net.openchrom.msd.converter.supplier.csv.cbi/pom.xml'
	build_project 'filternormalizer' 'https://github.com/OpenChrom/filternormalizer.git' 'filternormalizer/openchrom/cbi/net.openchrom.chromatogram.xxd.filter.supplier.normalizer.cbi/pom.xml'
	build_project 'asciiconverter' 'https://github.com/OpenChrom/asciiconverter.git' 'asciiconverter/openchrom/cbi/net.openchrom.xxd.converter.supplier.ascii.cbi/pom.xml'
	build_project 'molpeakident' 'https://github.com/OpenChrom/molpeakident.git' 'molpeakident/openchrom/cbi/net.openchrom.msd.classifier.supplier.molpeak.cbi/pom.xml'
	build_project 'ulan-openchrom' 'https://github.com/holyjan3/ulan-openchrom.git' 'ulan-openchrom/chemclipse/cbi/org.chromulan.system.control.cbi/pom.xml'
	#
	# Build order is important.
	#
	build_project 'org.eclipse.chemclipse.amdiscalri' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.amdiscalri.git' 'org.eclipse.chemclipse.amdiscalri/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.cbi/pom.xml'
	build_project 'cmsworkflow' 'https://github.com/OpenChrom/cmsworkflow.git' 'cmsworkflow/openchrom/cbi/net.openchrom.msd.process.supplier.cms.cbi/pom.xml'	
	build_project 'processworkflows' 'https://github.com/OpenChrom/processworkflows.git' 'processworkflows/openchrom/cbi/net.openchrom.chromatogram.xxd.process.supplier.workflows.cbi/pom.xml'
	build_project 'processalignment' 'https://github.com/OpenChrom/processalignment.git' 'processalignment/openchrom/cbi/net.openchrom.chromatogram.xxd.process.supplier.alignment.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.chemclipsequant' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipsequant.git' 'org.eclipse.chemclipse.chemclipsequant/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.cbi/pom.xml'
	build_project 'proteoms' 'https://github.com/OpenChrom/proteoms.git' 'proteoms/openchrom/cbi/net.openchrom.msd.identifier.supplier.proteoms.cbi/pom.xml'
	build_project 'opentyper' 'https://github.com/OpenChrom/opentyper.git' 'opentyper/openchrom/cbi/net.openchrom.msd.identifier.supplier.opentyper.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.classifierdurbinwatson' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.classifierdurbinwatson.git' 'org.eclipse.chemclipse.classifierdurbinwatson/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.peakdeconvdetec' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.peakdeconvdetec.git' 'org.eclipse.chemclipse.peakdeconvdetec/chemclipse/cbi/org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.cbi/pom.xml'
	build_project 'openchromjython' 'https://github.com/OpenChrom/openchromjython.git' 'openchromjython/openchrom/cbi/net.openchrom.chromatogram.msd.process.supplier.jython.cbi/pom.xml'
	build_project 'openchromgroovy' 'https://github.com/OpenChrom/openchromgroovy.git' 'openchromgroovy/openchrom/cbi/net.openchrom.chromatogram.msd.process.supplier.groovy.cbi/pom.xml'
	build_project 'org.eclipse.chemclipse.compilationbase' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.compilationbase.git' 'org.eclipse.chemclipse.compilationbase/chemclipse/cbi/org.eclipse.chemclipse.rcp.app.compilation.cbi/pom.xml'
	#
	# Product ChemClipse
	#
	build_project 'org.eclipse.chemclipse.chemclipse' 'git://git.eclipse.org/gitroot/chemclipse/org.eclipse.chemclipse.chemclipse.git' 'org.eclipse.chemclipse.chemclipse/chemclipse/cbi/org.eclipse.chemclipse.rcp.compilation.community.cbi/pom.xml'
	cd $active	
	cd org.eclipse.chemclipse.chemclipse/chemclipse/plugins/org.eclipse.chemclipse.rcp.compilation.community.product/target/products/org.eclipse.chemclipse.rcp.compilation.community.product.id/
	cp ../../../../../packaging/org.eclipse.chemclipse.rcp.compilation.community.packaging/run_packaging.sh run_packaging.sh
	chmod a+x run_packaging.sh
	./run_packaging.sh

	#
	# Product OpenChrom Community Edition
	#
	build_project 'openchromcomp' 'https://github.com/OpenChrom/openchromcomp.git' 'openchromcomp/openchrom/cbi/net.openchrom.rcp.compilation.community.cbi/pom.xml'
	cd $active
	cd openchromcomp/openchrom/plugins/net.openchrom.rcp.compilation.community.product/target/products/net.openchrom.rcp.compilation.community.product.id/
	cp ../../../../../packaging/net.openchrom.rcp.compilation.community.packaging/run_packaging.sh run_packaging.sh
	chmod a+x run_packaging.sh
	./run_packaging.sh

echo "--------------------------------------------"
echo "Build Finished Successfully"
echo "--------------------------------------------"
