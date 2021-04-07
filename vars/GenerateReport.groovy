@Grab('org.apache.commons:commons-csv:1.8')
import org.apache.commons.csv.*
import java.util.stream.*

@NonCPS
def call()
{

Reader filereader = new FileReader("$WORKSPACE\\Input.csv");
StringBuilder stringBuilder = new StringBuilder();
PrintWriter writer;
String[] inputHeader = ["AppID","AppName","Environment","ReleaseVersion","Status"]
String outputHeader="App ID,App Name,Release Version,Environments Passed,Environment Failed,Comments";
Iterable<CSVRecord> inputCSVRecordsIterable;
Map<String, List<CSVRecord>> groupedRecordMap
String passedEnvList;
String failedEnvList;
String comment;

stringBuilder.append(outputHeader);
inputCSVRecordsIterable = CSVFormat.DEFAULT
		.withHeader(inputHeader)
		.withFirstRecordAsHeader()
		.parse(filereader);

groupedRecordMap =   StreamSupport
		.stream(inputCSVRecordsIterable.spliterator(), false).
		collect(Collectors.groupingBy({record -> record.get("AppID")+"|"+record.get("AppName")+"|"+record.get("ReleaseVersion")} ));
		
for (Map.Entry<String, List<CSVRecord>> entry : groupedRecordMap.entrySet()) {
	println(entry.getKey());
	println(entry.getValue());
	passedEnvList = "Nil";
	failedEnvList = "Nil";
	comment = "NA";
	List<CSVRecord> recordList = entry.getValue();
	List<CSVRecord> failedRecord = recordList.stream().filter({f -> f.get("Status").contains("Failed")})
	.collect(Collectors.toList());
	List<CSVRecord> passedRecord = recordList.stream().filter({f -> !f.get("Status").contains("Failed")})
	.collect(Collectors.toList());
	if (passedRecord != null && passedRecord.size() > 0) {
		passedEnvList = passedRecord.stream().map({mp -> mp.get("Environment")}).collect(Collectors.joining("|"));
	}
	if (failedRecord != null && failedRecord.size() > 0) {
		failedEnvList = failedRecord.stream().map({mp -> mp.get("Status")}).collect(Collectors.joining("|"));
	}
	
	List<CSVRecord> prod = recordList.stream().filter({f -> f.get("Environment").contains("PROD")}).collect(Collectors.toList());
	List<CSVRecord> uat = recordList.stream().filter({f -> f.get("Environment").contains("UAT")}).collect(Collectors.toList());
	List<CSVRecord> sit = recordList.stream().filter({f -> f.get("Environment").contains("SIT")}).collect(Collectors.toList());
	List<CSVRecord> dev = recordList.stream().filter({f -> f.get("Environment").contains("DEV")}).collect(Collectors.toList());
	if(prod != null && prod.size() > 0){
		comment = prod.get(0).get("Status");
	}else if(uat != null && uat.size() > 0){
		comment = uat.get(0).get("Status");
	}else if(sit != null && sit.size() > 0){
		comment = sit.get(0).get("Status");
	}else if(dev != null && dev.size() > 0){
		comment = dev.get(0).get("Status");
	}
	stringBuilder.append('\n');
	stringBuilder.append(recordList.get(0).get("AppID") + ",");
	stringBuilder.append(recordList.get(0).get("AppName") + ",");
	stringBuilder.append(recordList.get(0).get("ReleaseVersion") + ",");
	stringBuilder.append(passedEnvList + ",");
	stringBuilder.append(failedEnvList + ",");
	stringBuilder.append(comment);
}

try {
	writer = new PrintWriter(new File("$WORKSPACE\\Release_Status.csv"))
	writer.write(stringBuilder.toString());	
	println ("The Output content:  ${stringBuilder}")
}
catch (FileNotFoundException e) {
	System.out.println(e.getMessage());
}
finally {
	writer.close();
}
}
