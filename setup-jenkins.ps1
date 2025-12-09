# Jenkins Setup Script for NutriSnap3 CI/CD
# This script sets up Jenkins with Docker for automated testing and building

Write-Host "Setting up Jenkins CI/CD Pipeline for NutriSnap3" -ForegroundColor Cyan
Write-Host ""

# Clean up any existing containers
Write-Host "Cleaning up existing containers..." -ForegroundColor Yellow
docker rm -f jenkins-blueocean jenkins-docker 2>$null
Write-Host ""

# Step 1: Create Docker network
Write-Host "Step 1: Creating Jenkins Docker network..." -ForegroundColor Yellow
docker network create jenkins 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "Network created successfully" -ForegroundColor Green
} else {
    Write-Host "Network already exists (this is fine)" -ForegroundColor Gray
}
Write-Host ""

# Step 2: Run Docker-in-Docker container
Write-Host "Step 2: Starting Docker-in-Docker container..." -ForegroundColor Yellow
docker run `
  --name jenkins-docker --rm --detach `
  --privileged --network jenkins --network-alias docker `
  --env DOCKER_TLS_CERTDIR=/certs `
  --volume jenkins-docker-certs:/certs/client `
  --volume jenkins-data:/var/jenkins_home `
  --publish 2376:2376 `
  docker:dind --storage-driver overlay2

if ($LASTEXITCODE -eq 0) {
    Write-Host "Docker-in-Docker container started" -ForegroundColor Green
} else {
    Write-Host "Failed to start Docker-in-Docker" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 3: Build custom Jenkins image
Write-Host "Step 3: Building custom Jenkins image with Blue Ocean..." -ForegroundColor Yellow
docker build -t myjenkins-blueocean:2.528.2-1 -f Dockerfile.jenkins .

if ($LASTEXITCODE -eq 0) {
    Write-Host "Jenkins image built successfully" -ForegroundColor Green
} else {
    Write-Host "Failed to build Jenkins image" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 4: Run Jenkins container (using port 9090 instead of 8080)
Write-Host "Step 4: Starting Jenkins container on port 9090..." -ForegroundColor Yellow
docker run `
  --name jenkins-blueocean --restart=on-failure --detach `
  --network jenkins --env DOCKER_HOST=tcp://docker:2376 `
  --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 `
  --publish 9090:8080 --publish 50000:50000 `
  --volume jenkins-data:/var/jenkins_home `
  --volume jenkins-docker-certs:/certs/client:ro `
  myjenkins-blueocean:2.528.2-1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Jenkins container started successfully" -ForegroundColor Green
} else {
    Write-Host "Failed to start Jenkins container" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 5: Wait for Jenkins to start
Write-Host "Step 5: Waiting for Jenkins to start (this may take a minute)..." -ForegroundColor Yellow
Start-Sleep -Seconds 40

# Step 6: Get initial admin password
Write-Host "Step 6: Retrieving initial admin password..." -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Jenkins Initial Admin Password:" -ForegroundColor Cyan
docker exec jenkins-blueocean cat /var/jenkins_home/secrets/initialAdminPassword
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Jenkins Setup Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "1. Open your browser and navigate to: http://localhost:9090" -ForegroundColor White
Write-Host "2. Use the password above to unlock Jenkins" -ForegroundColor White
Write-Host "3. Install suggested plugins" -ForegroundColor White
Write-Host "4. Create your first admin user" -ForegroundColor White
Write-Host "5. Create a new Pipeline job for NutriSnap3" -ForegroundColor White
Write-Host ""
Write-Host "To stop Jenkins:" -ForegroundColor Yellow
Write-Host "   docker stop jenkins-blueocean jenkins-docker" -ForegroundColor White
Write-Host ""
Write-Host "To view Jenkins logs:" -ForegroundColor Yellow
Write-Host "   docker logs -f jenkins-blueocean" -ForegroundColor White
Write-Host ""
