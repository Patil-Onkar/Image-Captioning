# Image Captioning  


## About  

This project is extension of 'https://github.com/Patil-Onkar/EnglishToHindi-Translator', Here attention mechanism is used to generate caption of an image. It is really fascinating to see how model generate text based on its focus.  

![image](https://user-images.githubusercontent.com/39105103/121126608-14f81280-c846-11eb-8578-be330208238b.png)  


From above image, it can be seen that how model focussed bird in image to predict word 'bird' and similarly for water. It is something that humans do.  
Here, I tried to reproduce same thing. This project is divided into two parts 1. Build and Train ML model 2. Deply it in mobile application.  

### 1. Build and Train ML Model:  

Model is constructed using an encoder and a decoder. Encoder encodes the image into a vector form, then an attention mechanism focuses specific part and map it to the vector that can represents it in human language. The output is then fed to decoder, that decodes and convert it into text.  

![image](https://user-images.githubusercontent.com/39105103/121139497-72945b00-c856-11eb-809f-912076cdca06.png)  

To train the model cocdataset is used. Dataset containes 82000 images each with atleast 5 captions.

Coco dataset is used to train the model. Model will first 


 
