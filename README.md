# Classifier Viewer

This project intends to dynamically create two input classifiers and visualize their training process.
<br>The <a href="https://github.com/iliasbk/NN_tools">neural network tools</a> project provides a complete basis for the classifiers implementation.  
<br><h3>Data</h3>
<br>The application allows users to randomly generate new data and define some of its parameters such as the number of classes, class size, number of clusters and more.
<br>The data is composed of pairs of values represented by coordinates in the XY plane. Each pair of values is a member of a certain class represented by a unique colour.  
<br><h3>Classifier</h3>
<br>The application allows users to dynamically modify the classifier by adding or removing layers from the network, and select an activation function for each of them.
<br>Here the classifier is simply a neural network with two inputs corresponding to the data value pair.
<br>The network learns the class distribution by minimising the squared error loss function.
<br>The output is a vector with the same number of elements as the number of classes. Each vector element stays for a certain class and represents the probability of the input to be of that class.

# Preview
[![SC2 Video](res/preview.gif)]([https://www.youtube.com/watch?v=--b-9HrKK6w](https://www.youtube.com/watch?v=-TkSJH7p__k)https://www.youtube.com/watch?v=-TkSJH7p__k)
<br>
<a href="https://www.youtube.com/watch?v=-TkSJH7p__k">Watch on YouTube</a>

# How to Set Up
1. Clone this project
2. Then clone the <a href="https://github.com/iliasbk/NN_tools">neural network tools</a> repository  
3. Add it to the build path of this project
4. You can then build the project, or simply run <i>classifier_viewer/src/viewer/App.java</i>
