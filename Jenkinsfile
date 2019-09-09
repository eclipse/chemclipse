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
		jdk 'adoptopenjdk-hotspot-jdk8-latest'
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
	}
}
