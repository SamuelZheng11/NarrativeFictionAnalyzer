# NarrativeFictionAnalyzer
Host source files for Alex and Sam's fourth year research project at the Faculty of Engineering, University of Auckland

# Hardware Requirements
- Machine running windows
- Machine with 16GB of RAM or greater
- Computer Monitor
- Intel Quad Core i5 processor or similar or more powerful

# Project Setup
1. Clone our repository or use the code in this submission

2. Install & Open Intellij IDEA (Community is the one we used to develop our application but any version should work)

3. Install Maven Dependancies (this should be done automatically, as specified by the maven xml, but it may ask you if you
	want to import changes, is so select "import maven changes when the pop up on the bottem right hand corner appears")

4. Make sure the project is running in Java8. toolbar -> file -> project structure -> project and select java 1.8

5. if some parts of the code are red you may need to define the path to the CoreNLP library. To do this go to the toolbar at
	the top select file -> project structure -> libraries and add a new java dependancy using the green "+" sign. then enter 
	the PATH_TO_FOLDER/Developed_Research_Components\Developed_Software\stanford-corenlp-full-2018-10-05 folder

6. If parts of the code is highlighted in red, change the source folder of the repository to 
	PATH_TO_FOLDER/Developed_Research_Components/Developed_Software/src. This can be done by going to Intellij's toolbar as 
	selecting file -> project structure -> modules and unassigning the selected source folder (if it is not src), 
	then selecting the src folder as the source 

7. To run the program go to toolbar -> run -> edit configurations and create a new "application" using the green "+" button.

8. Name the launch configuration what you want to call it and click on the "..." button at the end of "Main class:" field,
	there should be two options "Launcher" and "UILauncher". if you are intergrating with an external system "Launcher" is the 
	one you want. if you are further developing BookWyrm as a stand alone application you want the "UILauncher".

9. Click "Apply" and "OK"

10. you can now launch it from the top right hand corner using the green ">" (play arrow) button

11. The UI has 4 different options. viewing created models, analysing your custom text, read from TXT document and read from ePub
	for more documentation on how to use the application view the "Developed_Software_Documentation" file under 
	Developed_Reserach_Components folder
  
# Software Documentation
Please refer to the GitHub Wiki
