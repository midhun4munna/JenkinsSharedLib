def call()
{
emailext attachLog: true, attachmentsPattern: 'Release_Status.csv', body: '''Hi Team,

***Jenkins Pipeline Build Details!!!***

Job_Name :: CSVPipeline

Username :: Midhun M

#Please find the attached result [Release_Status.csv]. 

Regards,
Midhun M

''', subject: '[CSVPipeline-job] #BuildNumber : $BUILD_NUMBER #Status : $BUILD_STATUS', to: 'midhun.m1989@yahoo.com'
}
