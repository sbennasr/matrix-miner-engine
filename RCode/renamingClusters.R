library (arules)
library (arulesViz)
library (rstudio)
## MyData
## summary(MyData)

## reqTrans <- read.transactions(file="/home/user/git/familiar-language/FAMILIAR/R statistics/itemsets.csv", format = "basket", sep=";")
clist <- c("cluster1","cluster2.1", "cluster2.2", "cluster2.3", "cluster2.4", "cluster2.5", "cluster2.6", "cluster2.7", "cluster2.8", "cluster2.9", "cluster2.10", "cluste3.1", "cluster3.2", "cluster3.3", "cluster3.4", "cluster3.5", "cluster3.6", "cluster3.7", "cluster3.8", "cluster3.9", "cluster3.10", "cluster3.11", "cluster3.12", "cluster3.13", "cluster3.14", "cluster3.15", "cluster3.16", "cluster3.17", "cluster3.18", "cluster3.19", "cluster3.20")


matrix <- matrix(NA, 0, 2)
colnames(matrix) <- c("clusters", "items")
matrix

for (fileName in clist) {
  fileUrl <-  paste("/home/user/FinalSpace/VMiner/R statistics cnx/", fileName,".csv",sep = "")
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
  reqFIS <- apriori(reqTrans, parameter = list(supp = 0.4, conf = 0.9, target="maximally frequent itemsets"), appearance = NULL, control = NULL)
  
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
  write.table(dataForCsv, file = "/home/user/FinalSpace/VMiner/R statistics cnx/clustersFIS.csv", sep = ";",  col.names = NA,
              qmethod = "double")
}




