pipeline {
	agent {
		kubernetes {
			label 'centos-7'
		}
	}
	triggers {
		pollSCM('H/5 * * * *')
	}
	parameters {
		booleanParam(name: 'CLEAN_INTEGRATION', defaultValue: false, description: 'Attention: Cleans the integration folder with all branches completely.')
		booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: false, description: 'Clean the workspace before build')
		booleanParam(name: 'PUBLISH_PRODUCTS', defaultValue: false, description: 'Copy to the compiled products for Windows, macOS and Linux')
	}
	tools {
		maven 'apache-maven-latest'
		jdk   'adoptopenjdk-hotspot-jdk11-latest'
	}
	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '1'))
	}
	stages {
		stage ('clean workspace') {
			when {
				environment name: 'CLEAN_WORKSPACE', value: 'true'
			}
			steps {
				cleanWs notFailBuild: true
				//we must checkout again here because we have just cleared the data :-)
				checkout scm
			}
		}
		stage('build') {
			steps {
				wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
					sh 'mvn -B -Dtycho.localArtifacts=false -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -Dmaven.test.failure.ignore=true -Dmaven.repo.local=$WORKSPACE/.mvn -f chemclipse/releng/org.eclipse.chemclipse.aggregator/pom.xml clean install'
				}
				archiveArtifacts 'chemclipse/products/org.eclipse.chemclipse.rcp.compilation.community.product/target/products/*.zip,chemclipse/products/org.eclipse.chemclipse.rcp.compilation.community.product/target/products/*.tar.gz'
			}
		}
		stage('clean integration') {
			when {
				environment name: 'CLEAN_INTEGRATION', value: 'true'
			}
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
					sh "ssh genie.chemclipse@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/chemclipse/integration/"
				}
			}
		}
		stage('clean deploy') {
			when {
				environment name: 'CLEAN_WORKSPACE', value: 'true'
			}
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
					sh "ssh genie.chemclipse@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}"
				}
			}
		}
		stage('deploy') {
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
					sh '''
						#
						# Integration
						#
						ssh genie.chemclipse@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}/repository
						ssh genie.chemclipse@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}/downloads
						scp -r chemclipse/sites/org.eclipse.chemclipse.rcp.compilation.community.updateSite/target/repository/* genie.chemclipse@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}/repository
					'''
				}
			}
		}
		stage('publish') {
			when {
				environment name: 'PUBLISH_PRODUCTS', value: 'true'
			}
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
					sh '''					
						scp chemclipse/products/org.eclipse.chemclipse.rcp.compilation.community.product/target/products/org.eclipse.chemclipse.rcp.compilation.community.product.id-win32.win32.x86_64.zip genie.chemclipse@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}/downloads/chemclipse-win32.win32.x86_64.zip
						scp chemclipse/products/org.eclipse.chemclipse.rcp.compilation.community.product/target/products/org.eclipse.chemclipse.rcp.compilation.community.product.id-linux.gtk.x86_64.tar.gz genie.chemclipse@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}/downloads/chemclipse-linux.gtk.x86_64.tar.gz
						scp chemclipse/products/org.eclipse.chemclipse.rcp.compilation.community.product/target/products/org.eclipse.chemclipse.rcp.compilation.community.product.id-macosx.cocoa.x86_64.tar.gz genie.chemclipse@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/chemclipse/integration/${BRANCH_NAME}/downloads/chemclipse-macosx.cocoa.x86_64.tar.gz
					'''
				}
			}
		}
	}
}
