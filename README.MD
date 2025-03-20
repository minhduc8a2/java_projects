# Maven Project Setup

## 🚀 Creating a New Maven Project

To create a new Maven project using the quickstart archetype, open a terminal and run:

```sh
mvn archetype:generate -DgroupId=com.example -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

### 📌 Explanation:
- `-DgroupId=com.example` → Defines the package structure.
- `-DartifactId=my-app` → Sets the project name (a folder named `my-app` will be created).
- `-DarchetypeArtifactId=maven-archetype-quickstart` → Uses the default Java project template.
- `-DinteractiveMode=false` → Skips prompts for a quicker setup.

## 🔨 Compiling and Running the Project
Once the project is created, navigate to the project folder:

```sh
cd my-app
```

Compile the project:

```sh
mvn compile
```

Run the main class:

```sh
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### 📝 Notes:
- Ensure that `com.example.Main` is the correct path to your `main` class.
- If `exec:java` is not available, add the `exec-maven-plugin` to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>java</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 🎯 Next Steps
- Modify `Main.java` inside `src/main/java/com/example/Main.java`.
- Add dependencies in `pom.xml` as needed.
- Package your project with `mvn package`.

Happy coding! 🚀

