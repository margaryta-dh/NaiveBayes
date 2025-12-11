#Naive Bayes Text Classifier

This repository contains my own implementation of a Naive Bayes text classifier in Java. I built 
it because I wanted to understand the algorithm behind sentiment analysis on a deeper level,
how it actually works. It was also a great exercise in writing structured code
using a Maven project setup.
The result is a small but fully functional classifier that can analyze and categorize text: 
whether text contains positive or negative sentiment. 

##My goal

My goal was to create a machine learning model without using ML frameworks to show that I can:
- think algorithmically
- structure Java projects well
- implement mathematical concepts (probabilities, priors, train/test evaluation) in code.

I believe it is an important skill to understand how things work under the hood.

##What classifier can do

- Load labeled training data
- tokenize and preprocess text
- build a vocabulary and count word frequencies per class
- calculate prior and conditional probabilities
- classify new text
- apply stopword filtering

##Tech stack

- Java 17
- Maven
- Object-oriented design, with no external ML libraries

##Project structure

- "Data Loader" -reads training and test data
- "TextNorm" - handles text normalization
- "Main" - example run: train the model and classifies new text
- "NaiveBayes" - training and prediction logic
