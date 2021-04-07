#!/usr/bin/env groovy

def call(Map getval) {
	checkout([$class: 'GitSCM', 
	branches: [[ name: getval.branch ]], 
	userRemoteConfigs: [[credentialsId: 'githubcred', url: getval.url ]]
	])
  }

