# Health and Fitness Club Management System

## Group 128

## Authors: 
Ansh Sharma (101327077), Kaven Muraleitharan (101305963), Dean Sabbah (101199120)

## Repository:
https://github.com/KavenMuralei/COMP3005-Final-Project


## Videos:  
- [DDL/SQL](https://youtu.be/ynwafMYWMGc)
- [ER model and Relation Schema diagrams](https://youtu.be/cjnfWEdgspc)
- [Operation demonstrations](https://youtu.be/UExdMYRmKKM) / [Operation demonstrations ALT Link](https://drive.google.com/file/d/1xTgsrsqfd_oS1ZrIEb4E9XAQL1HftHiG/view?usp=drive_link) 


## Install and running:

### Database  
- Ensure that your Postgresql instance is running on port 5432 (Should be the default value)
- Make sure that the username and password to the database are 'postgres' and 'admin' respectively
- Create a new database named 'finalproject'
- To initialize the database you have to run the DDL.sql file on your PostgreSQL server. The DDL.sql file can be found in the sql folder of this project. 
- To populate the database you have to run the DML.sql file on your PostgreSQL server. The DML.sql file can also be found in the sql folder of this project

#### Using provided .jar stuff
Included in the submitted .zip file is an executable jar file. To run this, all you need to do is run the following command in the same directory as the jar file:  
`java -jar .\COMP3005-Final-Project.jar`

#### Git clone method
- Open up an IDE (IntelliJ, Visual Studio Code)
- Clone the git via the terminal and type `git clone <repository link>` or an option within the IDE (EX. 'Git' area in IntelliJ) using the repository link there
- If asked that a maven was detected, apply it (for IntelliJ, IDK about VSC)

Otherwise, you can take the following steps to build and run the file yourself.  
1. Select a code editor (has only been tested on VSCode using java extentions and Intelij)  
2. Open extracted project folder in code editor  
3. If prompted to import the project as a maven project, press accept  
4. Press the run button to build and run the project in the code editor


### Users Email
Preset Login Information for signing in

Member: deansabbah@cmail.carleton.ca

Trainer: anshsharma@cmail.carleton.ca

Admin: kavenmuraleitharan@cmail.carleton.ca

Password for all: password

Once you get into the program, the CLI will direct you. Most inputs on the user's part for menu navigation are either the numbers or the exact phrase beside the number. For most questions where they ask you to input data, make sure formatting is correct, like for dates (YYYY-MM-DD) and 24-hour (HH:MM) timings.
