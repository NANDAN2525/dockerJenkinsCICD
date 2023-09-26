def dockerImageName = 'nanda25/dockerjenkinscicd'
def emailRecipient = 'nandansrock@gmail.com'
def dockeruserName = 'nanda25'
def sonarCredentials = 'sonartoken'

pipeline {
    agent any

    tools {
        maven "Maven 3.8.6"
        jdk 'java17'
    }

    environment {
        DOCKER_LOGIN = credentials('dockerloginpwd')
        WORKSPACE_DIR = "${JENKINS_HOME}/workspace/${JOB_NAME}"
        DC_VERSION = 'latest'
        DC_DIRECTORY = "${env.WORKSPACE_DIR}/OWASP-Dependency-Check"
        DC_PROJECT = "dependency-check scan: ${env.WORKSPACE_DIR}"
        DATA_DIRECTORY = "${DC_DIRECTORY}/data"
        CACHE_DIRECTORY = "${DATA_DIRECTORY}/cache"
    }

   parameters {
    choice(
        // choices: ["Baseline", "Full"],
        choices: ["Baseline"],
        name: 'SCAN_TYPE', // Corrected parameter name
        description: 'Type of scan to perform inside the container'
    )
    string(
        defaultValue: "http://127.0.0.1:1119/",
        description: 'Target URL to scan',
        name: 'TARGET'
    )
    string(
        defaultValue: "http://127.0.0.1:9000/",
        description: 'Sonar host URL',
        name: 'SONARURL'
    )
    booleanParam(
        defaultValue: true,
        description: 'Generate report',
        name: 'GENERATE_REPORT'
    )
}
    stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '1', url: 'https://github.com/NANDAN2525/dockerJenkinsCICD.git']])
            }
        }

        stage('sonar') {
            steps {
                script {
                    withSonarQubeEnv(credentialsId: sonarCredentials) {
			bat "mvn clean package"
                        bat """
                            mvn sonar:sonar -Dsonar.host.url=${params.SONARURL} -Dsonar.token=\${env.${sonarCredentials}} -Dsonar.java.binaries=.
                        """
			archiveArtifacts artifacts: "target/surefire-reports/**/*.xml", allowEmptyArchive: true
                    }
                }
            }
        }
  stage('OWASP Dependency-Check Vulnerabilities') {
      steps {
                script {
                    // Pull the latest version of OWASP Dependency-Check Docker image
                    bat "docker pull owasp/dependency-check:${DC_VERSION}"

                    // Run Dependency-Check inside a Docker container
                    bat """docker run --rm ^
                        --volume ${env.WORKSPACE}:/src ^
                        --volume ${DATA_DIRECTORY}:/usr/share/dependency-check/data ^
                        --volume ${env.WORKSPACE}/odc-reports:/report ^
                        owasp/dependency-check:${DC_VERSION} ^
                        --scan /src ^
                        --format "HTML" ^
                        --project "${DC_PROJECT}" ^
                        --out /report
                    """
		  archiveArtifacts artifacts: "odc-reports/*.html", allowEmptyArchive: true
                }
            }
    }

        stage('Mvn Build') {
            steps {
                bat "mvn clean install"
            }
        }
        stage('Docker Image Build') {
            steps {
                script {
                    bat "docker build -t ${dockerImageName} ."
                }
            }
        }
stage("Trivy Scan") {
        steps {
        script {
            def reportDirectory = "${WORKSPACE_DIR}/trivy"

         bat """if not exist "${reportDirectory}" mkdir "${reportDirectory}" """

            // Run Trivy scan and save the JSON report
            bat """
                docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v "${reportDirectory}":/workspace aquasec/trivy image ${dockerImageName} --no-progress --scanners vuln --exit-code 0 --severity HIGH,CRITICAL --format json --output /workspace/trivy_report.json
            """
            // Archive the Trivy HTML report as a build artifact
           archiveArtifacts artifacts: "trivy/*.json", allowEmptyArchive: true
        }
    }
}
        stage('owasp zap Scan') {
            steps {
                script {
                    echo "<-- Parameter Initialization -->"
                    echo """
                    The current parameters are:
                        Scan Type: ${params.SCAN_TYPE}
                        Target: ${params.TARGET}
                        Generate report: ${params.GENERATE_REPORT}
                    """
                    echo "Pulling the latest OWASP ZAP container --> Start"
                    bat 'docker pull owasp/zap2docker-stable'
                    echo "Pulling the latest OWASP ZAP container --> End"
                    echo "Starting the container --> Start"
                    bat """
                    docker run --network host -dt --name owasp \
                    owasp/zap2docker-stable \
                    /bin/bash
                    """
                }
            }
        }

        stage('owasp scan Prepare wrk directory') {
            when {
                expression { params.GENERATE_REPORT }
            }
            steps {
                script {
                    bat """
                        docker exec owasp \
                        mkdir /zap/wrk
                    """
                    echo "----> Scan type: ${params.SCAN_TYPE}"
					 stage('Scanning target on OWASP container') {
       
                    scan_type = "${params.SCAN_TYPE}"
                    echo "----> Scan type: $scan_type"
                    target = "${params.TARGET}"
                    if (scan_type == "Baseline") {
                        bat """
                            docker exec owasp \
                            zap-baseline.py \
                            -t $target \
                            -r report.html \
                            -I
                        """
                    } else if (scan_type == "APIS") {
                        bat """
                            docker exec owasp \
                            zap-api-scan.py \
                            -t $target \
                            -x report.xml \
                            -I
                        """
                    } else if (scan_type == "Full") {
                        bat """
                            docker exec owasp \
                            zap-full-scan.py \
                            -t $target \
                            -I
                        """
                        // -x report-$(date +%d-%b-%Y).xml
                    } else {
                        echo "Something went wrong..."
                    }
                }
        
					
                    echo "copying the workspace"
                    bat """
                        docker cp owasp:/zap/wrk/ ${WORKSPACE_DIR}
                    """
		archiveArtifacts artifacts: "wrk/*.html", allowEmptyArchive: true
                }
            }
        }


        stage('Docker Image push to hub') {
            steps {
                script {
                    env.DOCKER_PASSWORD = DOCKER_LOGIN
                    bat "docker login -u ${dockeruserName} -p %DOCKER_PASSWORD%"
                    bat "docker push ${dockerImageName}"
                }
            }
        }
    }

    post {
        failure {
            echo "Pipeline failed! Sending email notification..."
            emailext(
                subject: "Pipeline Failed: ${currentBuild.fullDisplayName}",
                body: """The Jenkins pipeline ${currentBuild.fullDisplayName} has failed.
                Pipeline URL: ${env.BUILD_URL}
                Error Details: ${currentBuild.rawBuild.getLog(1000)}""",
                to: "${emailRecipient}",
                replyTo: "${emailRecipient}",
                attachLog: true
            )
        }
        success {
		echo "Removing container"
            	bat '''
                docker stop owasp
                docker rm owasp
            	'''
 plot([ [series: [[file: "target/surefire-reports/**/*.xml", parser: 'JUnit', label: 'Test Cases'],
                    ],
                    title: 'Test Case Trend',
                    xaxis: 'Build Number',
                    yaxis: 'Test Cases Count',
                    group: 'Test Cases',
                    style: 'line'
                    ]]
publishHTML([
    allowMissing: false,
    alwaysLinkToLastBuild: false,
    keepAll: true,
    reportDir: "wrk", 
    reportFiles: "report.html",
    reportName: "OWASP ZAP Report",
    reportTitles: ""
])
publishHTML([
    allowMissing: false,
    alwaysLinkToLastBuild: false,
    keepAll: true,
    reportDir: "odc-reports",
    reportFiles: 'dependency-check-jenkins.html',
    reportName: 'OWASP Dependency jenkins.html',
    reportTitles: ''
])
publishHTML([
    allowMissing: false,
    alwaysLinkToLastBuild: false,
    keepAll: true,
    reportDir: "odc-reports",
    reportFiles: 'dependency-check-report.html',
    reportName: 'OWASP Dependency Check Report',
    reportTitles: ''
])
            emailext(
                subject: "Pipeline Succeeded: ${currentBuild.fullDisplayName}",
                body: """The Jenkins pipeline ${currentBuild.fullDisplayName} has succeeded.
                Pipeline URL: ${env.BUILD_URL}
                Attached the OWASP ZAP scan reports:""",
                to: "${emailRecipient}",
                replyTo: "${emailRecipient}",
                attachmentsPattern: '**/report.html,**/trivy_report.json,**/dependency-check-jenkins.html,**/dependency-check-report.html,**/publishedSuppressions.xml',
                attachLog: true
            )
        }
    }}
