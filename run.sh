chmod +x run.sh
#!/bin/bash
# Build and run the Docker containers in detached mode

# Build the Docker image and start the services
#!/bin/bash
set -e

echo "Starting Gradle clean build..."
./gradlew clean build

echo "Starting Docker Compose..."
docker-compose up --build -d

echo "Docker containers status:"
docker-compose ps
