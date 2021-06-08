# Image Captioning  


## About  

This project is extension of 'https://github.com/Patil-Onkar/EnglishToHindi-Translator', Here attention mechanism is used to generate caption of an image. It is really fascinating to see how model generate text based on its focus.  

![image](https://user-images.githubusercontent.com/39105103/121126608-14f81280-c846-11eb-8578-be330208238b.png)  


From above image, it can be seen that how model focussed bird in image to predict word 'bird' and similarly for water. It is something that humans do.  
Here, I tried to reproduce same thing. This project is divided into two parts 1. Build and Train ML model 2. Deply it in mobile application.  

### 1. Build and Train ML Model:  

Model is constructed using an encoder and a decoder. Encoder encodes the image into a vector form, then an attention mechanism focuses specific part and map it to the vector that can represents it in human language. The output is then fed to decoder, that decodes and convert it into text.  

![image](https://user-images.githubusercontent.com/39105103/121139497-72945b00-c856-11eb-809f-912076cdca06.png)  

To train the model, cocodataset is used. Dataset containes 82000 images each with atleast 5 captions. To encode the model, Inception V3 model s used. Inceptionv3 is build to classify images, but we can use its features as a encoded vectores.  

### 2. Deploy it in android application:  

Tensorflow provides many ways to put ML model into a production. In this case we convert it into tflite and embed it into android applications.  

![image](https://user-images.githubusercontent.com/39105103/121170933-7d111d80-c873-11eb-9b80-840400bb8e53.png)  

As shown in above flow diagram, app is linked to mobile's camera and gallary to take images. Images taken from camera/Gallery are converted to its RGB values and then normalized. Finally, this normalized RGB values are processed by tflite model and captions are generated.  


## How to use it:  

### a. Install apk file from https://drive.google.com/file/d/1vGWtQxHGLZ0G14UxOImXyVP9C1UXfgPM/view?usp=sharing  


### b. Use this framework to build image captioning app. To do so, following are the steps:  

   1. Go to --> 'ML model/image captioning.ipynb' tweak some parameters or use different model, use the model that give highest accuracy.  
      Running all the cells will create :  
      
        - TF saved model  
        - .Tflite model  
        - index to word dictionary in json  

   2. Copy .Tflite model and json file created in step 1 and paste it in --> 'Source code\app\src\main\assets'  

   3. Running the source code in android studio will create image captioning app.  

## Future Work:  

  - Try different attention mechanisms.  
  - Use transformers or BERT.  
  - Change encoder.  


        -

Coco dataset is used to train the model. Model will first 


 
