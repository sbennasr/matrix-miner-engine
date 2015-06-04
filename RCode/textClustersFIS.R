library (Matrix)
library (arules)
library (arulesViz)
library (rstudio)
## MyData
## summary(MyData)
args <- commandArgs(trailingOnly = TRUE)

## reqTrans <- read.transactions(file="/home/user/git/familiar-language/FAMILIAR/R statistics/itemsets.csv", format = "basket", sep=";")
clist <- c("cluster2.1", "cluster2.2", "cluster2.3", "cluster2.4", "cluster2.5", "cluster2.6", "cluster2.7", "cluster2.8", "cluster2.9", "cluster2.10", "cluster2.11", "cluster2.12", "cluster2.13", "cluster2.14", "cluster2.15", "cluster2.16", "cluster2.17", "cluster2.18", "cluster2.19", "cluster2.20", "cluster2.21", "cluster2.22", "cluster2.23", "cluster2.24", "cluster2.25", "cluster2.26", "cluster2.27", "cluster2.28", "cluster2.29", "cluster2.30", "cluster2.31", "cluster2.32", "cluster2.33", "cluster2.34", "cluster2.35", "cluster2.36", "cluster2.37", "cluster2.38", "cluster2.39", "cluster2.40", "cluster2.41", "cluster2.42", "cluster2.43", "cluster2.44", "cluster2.45", "cluster2.46", "cluster2.47", "cluster2.48", "cluster2.49", "cluster2.50", "cluster2.51", "cluster2.52", "cluster2.53", "cluster2.54", "cluster2.55", "cluster2.56", "cluster2.57", "cluster2.58", "cluster2.59", "cluster2.60", "cluster2.61", "cluster2.62", "cluster2.63", "cluster2.64", "cluster2.65", "cluster2.66", "cluster2.67", "cluster2.68", "cluster2.69", "cluster2.70", "cluster2.71", "cluster2.72", "cluster2.73", "cluster2.74", "cluster2.75", "cluster2.76", "cluster2.77", "cluster2.78", "cluster2.79", "cluster2.80", "cluster2.81", "cluster2.82", "cluster2.83", "cluster2.84", "cluster2.85", "cluster2.86", "cluster2.87", "cluster2.88", "cluster2.89", "cluster2.90", "cluster2.91", "cluster2.92", "cluster2.93", "cluster2.94", "cluster2.95", "cluster2.96", "cluster2.97", "cluster2.98", "cluster2.99", "cluster2.100", "cluster2.101", "cluster2.102", "cluster2.103", "cluster2.104", "cluster2.105", "cluster2.106", "cluster2.107", "cluster2.108", "cluster2.109", "cluster2.110", "cluster2.111", "cluster2.112", "cluster2.113", "cluster2.114", "cluster2.115", "cluster2.116", "cluster2.117", "cluster2.118", "cluster2.119", "cluster2.120", "cluster2.121", "cluster2.122", "cluster2.123", "cluster2.124", "cluster2.125", "cluster2.126", "cluster2.127", "cluster2.128", "cluster2.129", "cluster2.130", "cluster2.131", "cluster2.132", "cluster2.133", "cluster2.134", "cluster2.135", "cluster2.136", "cluster2.137", "cluster2.138", "cluster2.139", "cluster2.140", "cluster2.141", "cluster2.142", "cluster2.143", "cluster2.144", "cluster2.145", "cluster2.146", "cluster2.147", "cluster2.148", "cluster2.149", "cluster2.150", "cluster2.151", "cluster2.152", "cluster2.153", "cluster2.154", "cluster2.155", "cluster2.156", "cluster2.157", "cluster2.158", "cluster2.159", "cluster2.160", "cluster2.161", "cluster2.162", "cluster2.163", "cluster2.164", "cluster2.165", "cluster2.166", "cluster2.167", "cluster2.168", "cluster2.169", "cluster2.170", "cluster2.171", "cluster2.172", "cluster2.173", "cluster2.174", "cluster2.175", "cluster2.176", "cluster2.177", "cluster2.178", "cluster2.179", "cluster2.180", "cluster2.181", "cluster2.182", "cluster2.183", "cluster2.184", "cluster2.185", "cluster2.186", "cluster2.187", "cluster2.188", "cluster2.189", "cluster2.190", "cluster2.191", "cluster2.192", "cluster2.193", "cluster2.194", "cluster2.195", "cluster2.196", "cluster2.197", "cluster2.198", "cluster2.199", "cluster2.200")


matrix <- matrix(NA, 0, 2)
colnames(matrix) <- c("clusters", "items")
matrix
#setwd('C:\\Users\\sbennasr\\FinalSpace\\VMiner\\RResults\\TextFeatures')
setwd(paste(args[1],"\\TextFeatures", sep=""))

for (fileName in clist) {
  fileUrl <-  paste(fileName,".csv",sep = "")
  if (file.exists(fileUrl)){
  # Read CSV into R
  MyData <- read.csv(fileUrl, header=TRUE, sep=";",row.names=1)
  temp = read.csv(fileUrl, sep=";", row.names=1)
  
  temp1 <- as.matrix(temp) 
  
  reqTrans <- as(temp1, "transactions")
  
  ## coercing an object to a given class: create  transactions from matrix.
  inspect(reqTrans)
  supp = dim(MyData)[1]/7
  ## find and some frequent itemsets
  ##reqFIS <- eclat(reqTrans, parameter = list(supp = 0.4, target="maximally frequent itemsets"))
  reqFIS <- apriori(reqTrans, parameter = list(supp = 1, conf = 0.9, target="maximally frequent itemsets"), appearance = NULL, control = NULL)
  
  inspect(sort(reqFIS, by="support"))
  matrix <- rbind(list(t(as(rownames(MyData), "list")), t(as(labels(reqFIS), "list"))) , matrix)
  matrix  
  ## find maximal frequent itemsets
  ## maxReqFIS <- is.maximal(reqFIS)
  ## maxReqFIS
  
  
  ## count support in the database
  ## support(items(reqFIS), reqTrans)
  
  data <- data.frame(matrix)
  dataForCsv<- data.frame(lapply(data, as.character), stringsAsFactors=FALSE)
  write.table(dataForCsv, file = "clustersFIS.csv", sep = ";",  col.names = NA,
              qmethod = "double")
}
}


