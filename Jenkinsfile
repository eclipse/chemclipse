pipeline {
	agent {
		kubernetes {
			label 'ui-test'
		}
	}
	triggers {
		pollSCM('H/5 * * * *')
	}
	parameters {
		booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: false, description: 'Clean the workspace before build')
    }
    tools {
		maven 'apache-maven-latest'
		jdk   'oracle-jdk8-latest'
	}
	stages {
		stage ('clear workspace') {
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
			}
		}
		stage('clear deploy') {
			when {
				environment name: 'CLEAN_WORKSPACE', value: 'true'
			}
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
					sh "ssh genie.chemclipse@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/chemclipse/${BRANCH_NAME}"
				}
			}
		}
		stage('deploy') {
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
					sh '''
						ssh genie.chemclipse@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/chemclipse/${BRANCH_NAME}/repository
						ssh genie.chemclipse@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/chemclipse/${BRANCH_NAME}/downloads
						scp -r chemclipse/sites/org.eclipse.chemclipse.rcp.compilation.community.updateSite/target/repository/* genie.chemclipse@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/chemclipse/${BRANCH_NAME}/repository
					'''
				}
			}
		}
	}
}
