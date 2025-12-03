# COMP 3005 Final Project

## Group 128

## Authors: 
Ansh Sharma, Kaven Muraleitharan, Dean Sabbah

## Repository:
https://github.com/KavenMuralei/COMP3005-Final-Project


## Video:  


## Database installation
It is important to know that the code requires a PostgreSQL server with the following requirements:  
- Enssure that your Postgresql instance is running on port 5432 (Should be the default value),
- Make sure that the username and password to the database are 'postgres' and 'admin' respectively,
- Create a new database named 'finalproject'
- NOTE: If your PostgreSQL database doesn't have the following then the program will not run! If you are having trouble setting this up you can try running the program with an IDE and inserting your own url, username, and password in in the main function of Main.java.  
- To initialize the database you have to run the DDL.sql file on your PostgreSQL server. The DDL.sql file can be found in the sql folder of this project. 
- To populate the database you have to run the DML.sql file on your PostgreSQL server. The DML.sql file can also be found in the sql folder of this project



## Running the program:  
Included in submitted zip file is an executable jar file. To run this all you need to do is run the following command in the same directory as the jar file:  
`java -jar .\COMP3005-Final-Project.jar`

Otherwise, you can take the following steps to build and run the file yourself.
1. Ensure you have completed the database instructions above.
2. Select a code editor (has only been tested on VSCode using java extentions and Intelij)  
3. Open extracted project folder in code editor  
4. If prompted to import the project as a maven project, press accept  
5. Press the run button to build and run the project in the code editor  


