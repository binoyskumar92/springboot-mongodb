Apologies for the confusion. Here's your copy-pastable markdown `README.md` content:

```markdown
# Spring Data Sample Code Repository

This repository holds a sample program for testing out Spring Data.

## Steps to Setup

1. **Clone the Repository**

    Use the command below to clone the repository:

    ```
    git clone https://github.com/binoyskumar92/springboot-mongodb.git
    ```

2. **Edit the Database Connection URL**

    Open the `application.properties` file and replace `spring.data.mongodb.uri` with your MongoDB server URL. If you're using MongoDB Atlas, remember to use an older version of the driver.

    Sample URL:

    ```
    mongodb://<username>:<password>@test-shard-00-00.qsdfr.mongodb.net:27017,test-shard-00-01.qsdfr..mongodb.net:27017,test-shard-00-02.qsdfr.mongodb.net:27017/?ssl=true&replicaSet=atlas-zwm19k-shard-0&authSource=admin&retryWrites=true&w=majority
    ```

3. **Build the Project**

    Run the following command:

    ```
    mvn clean install
    ```

4. **Run the Application**

    Use the command below to start the app:

    ```
    mvn spring-boot:run
    ```

5. **Access the Swagger UI**

    You can find the Swagger UI at `http://localhost:8080/swagger-ui/#/`. Check if the port 8080 is free by running `sudo lsof -i :8080` and terminate any running processes if necessary.

6. **Create a Person**

    Navigate to the `PersonController` and try the POST request, replacing the value in the body. This will return a `personId`.

7. **Retrieve Person Information**

    Use the `getPersonStartWith` method to retrieve data. Just pass the `personId` retrieved from the previous step.

Happy coding with Spring Data!
```
