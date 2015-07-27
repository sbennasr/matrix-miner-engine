# matrix-miner-engine

Matrix miner engine automatically synthesizes product comparison matrices (PCMs) from a set of product descriptions written in natural language. Matrix miner engine is capable of identifying and organizing features and values in a PCM – despite the informality and absence of structure in the textual descriptions of products.

## Development Tools:

* Stanford CoreNLP: integrates many NLP tools, including the PartOf-Speech (POS) tagger that reads text in some language and assigns parts of speech to each word (and other token), such as noun, verb, adjective, etc. 
* Lucene: is a widely used Information Retrieval (IR) library to tokenize and remove stop words from text
* Simmetrics library : to compute Smith-Waterman and Levenshtein syntactical similarity measures.

## Getting started :

## Install

```
git clone https://github.com/sbennasr/matrix-miner-engine
```
Import the project into [eclipse] (https://eclipse.org/downloads/)

To compile the project you must define the environment variable JAVA_HOME which must point to a JDK with a version >= 7

Matrix miner engine requires models from standford NLP software that are available here : [models](http://nlp.stanford.edu/software/stanford-corenlp-v1.0.3.tgz)

The specific file is named stanford-corenlp-models-2011-04-11.jar and should be placed in the lib directory of the project.

The directory *matrixminer/test/MatrixMinerTest.java* contains an example that shows how to use the project.
