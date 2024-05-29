# Contributing to Eclipse ChemClipse

Thanks for your interest in this project.

## Project description

Eclipse ChemClipse supports the user to analyse data acquired from systems used
in analytical chemistry. In particular, chromatography coupled with mass
spectrometry (GC/MS) or flame-ionization detectors (GC/FID) is used to identify
and/or monitor chemical substances. It's an important task e.g. for quality
control issues.

* https://projects.eclipse.org/projects/science.chemclipse

## Developer resources

Information regarding source code management, builds, coding standards, and
more.

* https://github.com/eclipse/chemclipse/wiki/Chemclipse-Developer-Manual

The project maintains the following source code repositories

* https://github.com/eclipse/chemclipse

This project uses Github Issues to track ongoing development and issues.

* Search for issues:
   https://github.com/eclipse/chemclipse/issues
* Create a new report:
   https://github.com/eclipse/chemclipse/issues/new

Be sure to search for existing bugs before you create another one. Remember that
contributions are always welcome!

### Building the source
+ An OpenJDK is required. We recommend the [Eclipse Temurin](https://adoptium.net/temurin/) distribution.
+ Clone the repository: `git clone -b develop git@github.com:eclipse/chemclipse.git`
+ Build: mvn `mvn -f chemclipse/chemclipse/releng/org.eclipse.chemclipse.aggregator/pom.xml install`
+ Find the compiled product in `chemclipse/chemclipse/products/org.eclipse.chemclipse.rcp.compilation.community.product/target/products`

## Eclipse Contributor Agreement

Before your contribution can be accepted by the project team contributors must
electronically sign the Eclipse Contributor Agreement (ECA).

* http://www.eclipse.org/legal/ECA.php

Commits that are provided by non-committers must have a Signed-off-by field in
the footer indicating that the author is aware of the terms by which the
contribution has been provided to the project. The non-committer must
additionally have an Eclipse Foundation account and must have a signed Eclipse
Contributor Agreement (ECA) on file.

For more information, please see the Eclipse Committer Handbook:
https://www.eclipse.org/projects/handbook/#resources-commit

## Contact

Contact the project developers via the project's "dev" list.

* https://dev.eclipse.org/mailman/listinfo/chemclipse-dev

