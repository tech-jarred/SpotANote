Is this working?

Yes - HP

# HOW TO COMPILE CODE #
Run ./mvnw clean compile -- this will delete the target folder and its contents before recompiling .java files and rebuilding the folder

# HOW TO RUN PROGRAM #
Note that App.java is the driver class
In the terminal, run: ./mvnw exec:java -Dexec.mainClass="com.spotanote.App" -- this will run the program

*Note: For both of the above, this works on MacOS/Linux. For Windows, replace ./mvnw with .\mvnw*