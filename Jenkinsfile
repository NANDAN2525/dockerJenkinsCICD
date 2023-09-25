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
                    }
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
           archiveArtifacts artifacts: "${WORKSPACE_DIR}/trivy/*", allowEmptyArchive: true
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
        always {
            echo "Removing container"
            bat '''
                docker stop owasp
                docker rm owasp
            '''
        }
        failure {
            echo "Pipeline failed! Sending email notification..."
            emailext(
                subject: "Pipeline Failed: ${currentBuild.fullDisplayName}",
                body: """The Jenkins pipeline ${currentBuild.fullDisplayName} has failed.
                Pipeline URL: ${env.BUILD_URL}
                Error Details: ${currentBuild.rawBuild.getLog(1000)}""",
                to: "${emailRecipient}",
                replyTo: "${emailRecipient}",
                attachmentsPattern: '**/report.html,**/trivy_report.json',
                attachLog: true
            )
        }
        success {
            emailext(
                subject: "Pipeline Succeeded: ${currentBuild.fullDisplayName}",
                body: """The Jenkins pipeline ${currentBuild.fullDisplayName} has succeeded.
                Pipeline URL: ${env.BUILD_URL}
                Attached the OWASP ZAP scan reports:""",
                to: "${emailRecipient}",
                replyTo: "${emailRecipient}",
                attachmentsPattern: '**/report.html,**/trivy_report.json',
                attachLog: true
            )
        }
    }}