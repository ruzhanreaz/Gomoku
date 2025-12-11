# Gomoku

## How to Run

### 1. Make sure Java is installed
Check Java on your system:

```
java -version
```

You should see output containing the Java version. Java 11 or newer is recommended.

### 2. Download the JAR
If a compiled JAR is provided in this repository, it will be named `gomoku-fat.jar`.

- **Download**: get `gomoku-fat.jar` from the repository releases or the project root (if present).

### 3. Run the Game / AI
Open a terminal in the folder containing `gomoku-fat.jar` and run:

```
java -jar gomoku-fat.jar
```

### Build from source (optional)
To build the fat JAR yourself (Windows PowerShell):

```
.\mvnw.cmd clean package
```

After a successful build the JAR should be available under `target\` (for example `target\gomoku-fat.jar`). Run it with the same `java -jar` command above.

### Troubleshooting
- **No Java found**: install a JDK from AdoptOpenJDK, Temurin, or Oracle and retry.
- **JAR not found**: ensure you built the project or downloaded the correct artifact.
- **Permission issues**: run the terminal as a user with appropriate permissions.

If you want, I can also add a link to a release or add build artifacts to the repository — tell me which you prefer.
